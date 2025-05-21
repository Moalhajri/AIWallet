package com.example.walletai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val budgetDao: BudgetDao
) : ViewModel() {

    private val _categoryTotals = MutableStateFlow<List<CategoryTotal>>(emptyList())
    val categoryTotals: StateFlow<List<CategoryTotal>> = _categoryTotals.asStateFlow()

    private val _totalExpenses = MutableStateFlow(0.0)
    val totalExpenses: StateFlow<Double> = _totalExpenses.asStateFlow()

    private val _totalBudget = MutableStateFlow(0.0)
    val totalBudget: StateFlow<Double> = _totalBudget.asStateFlow()

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            expenseDao.getCategoryTotals().collect { totals ->
                _categoryTotals.value = totals
            }

            expenseDao.getTotalExpenses().collect { total ->
                _totalExpenses.value = total ?: 0.0
            }

            budgetDao.getAllBudgets().collect { budgets ->
                _totalBudget.value = budgets.sumOf { it.amount }
            }

            expenseDao.getAllExpenses().collect { expenseList ->
                _expenses.value = expenseList
            }
        }
    }
}