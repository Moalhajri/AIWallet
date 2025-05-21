package com.example.walletai

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val budgetDao: BudgetDao,
    private val aiBankStatementAnalyzer: AIBankStatementAnalyzer
) : ViewModel() {

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    suspend fun setInitialBudgetAndGoal(initialBudget: Double, goal: FinancialGoal) {
        userPreferences.saveFinancialGoal(goal)
        budgetDao.insertBudget(Budget(category = "General", amount = initialBudget, period = BudgetPeriod.MONTHLY))
        userPreferences.markOnboardingCompleted()
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