package com.example.walletai

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val expenseCategorizer: ExpenseCategorizer,
    private val aiBankStatementAnalyzer: AIBankStatementAnalyzer,
    application: Application
) : AndroidViewModel(application) {

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses

    private val _expenseTemplates = MutableStateFlow<List<ExpenseTemplate>>(emptyList())
    val expenseTemplates: StateFlow<List<ExpenseTemplate>> = _expenseTemplates

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private val aiExpensePredictor = AIExpensePredictor(getApplication())
    private val aiVoiceParser = AIVoiceParser(getApplication())

    init {
        loadExpenses()
        generateExpenseTemplates()
    }

    private fun loadExpenses() {
        viewModelScope.launch {
            expenseDao.getAllExpenses().collect {
                _expenses.value = it
            }
        }
    }

    private fun generateExpenseTemplates() {
        viewModelScope.launch {
            val recentExpenses = expenseDao.getRecentExpenses(20)
            val templates = recentExpenses
                .groupBy { it.category }
                .map { (category, expenses) ->
                    val avgAmount = expenses.map { it.amount }.average()
                    ExpenseTemplate(
                        description = "Average $category",
                        amount = avgAmount,
                        category = category
                    )
                }.toMutableList()

            val aiPredictedTemplate = aiExpensePredictor.predictNextExpense(recentExpenses)
            templates.add(aiPredictedTemplate)

            _expenseTemplates.value = templates
        }
    }fun processReceipt(uri: Uri) {
        viewModelScope.launch {
            try {
                // Process the receipt image and create an expense
                // For now, we'll just create a placeholder expense
                addExpense(
                    description = "Receipt Expense",
                    amount = 0.0,
                    category = "Uncategorized",
                    date = System.currentTimeMillis(),
                    tags = emptyList(),
                    receiptUri = uri
                )
            } catch (e: Exception) {
                // Handle error
                _message.value = "Failed to process receipt: ${e.message}"
            }
        }
    }

    fun addExpense(description: String, amount: Double, category: String, date: Long, tags: List<String>, receiptUri: Uri?) {
        viewModelScope.launch {
            val smartCategory = expenseCategorizer.categorizeExpense(description)
            val expense = Expense(
                description = description,
                amount = amount,
                category = smartCategory,
                date = date,
                tags = tags.joinToString(","),
                receiptPath = receiptUri?.toString() ?: ""
            )
            expenseDao.insertExpense(expense)
            _message.value = "Expense added successfully"
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            expenseDao.deleteExpense(expense)
            _message.value = "Expense deleted"
        }
    }

    fun parseVoiceInput(input: String) {
        viewModelScope.launch {
            try {
                val expense = aiVoiceParser.parseVoiceInput(input)
                expense?.let {
                    addExpense(it.description, it.amount, it.category, it.date, emptyList(), null)
                    _message.value = "Voice input processed: ${it.description}"
                } ?: run {
                    _message.value = "Unable to process voice input"
                }
            } catch (e: Exception) {
                _message.value = "Error processing voice input: ${e.message}"
            }
        }
    }

    fun uploadBankStatement(uri: Uri) {
        viewModelScope.launch {
            try {
                val expensesAdded = aiBankStatementAnalyzer.analyzeBankStatement(uri)
                _message.value = "Bank statement processed: $expensesAdded expenses added"
            } catch (e: Exception) {
                _message.value = "Error processing bank statement: ${e.message}"
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}