package com.example.walletai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val budgetDao: BudgetDao,
    private val budgetPredictor: BudgetPredictor
) : ViewModel() {

    private val _budgets = MutableStateFlow<List<Budget>>(emptyList())
    val budgets: StateFlow<List<Budget>> = _budgets.asStateFlow()

    private val _generalBudget = MutableStateFlow<Budget?>(null)
    val generalBudget: StateFlow<Budget?> = _generalBudget.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                loadBudgets()
                loadGeneralBudget()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadBudgets() {
        budgetDao.getAllBudgets().collect { allBudgets ->
            _budgets.value = allBudgets.filter { it.category != "General" }
        }
    }

    private suspend fun loadGeneralBudget() {
        budgetDao.getBudgetForCategory("General").collect { budget ->
            _generalBudget.value = budget
        }
    }

    fun addBudget(category: String, amount: Double, period: BudgetPeriod, isGeneralBudget: Boolean = false) {
        viewModelScope.launch {
            try {
                val budget = Budget(
                    category = if (isGeneralBudget) "General" else category,
                    amount = amount,
                    period = period
                )
                budgetDao.insertBudget(budget)

                if (isGeneralBudget) {
                    loadGeneralBudget()
                } else {
                    loadBudgets()
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun updateBudget(budget: Budget) {
        viewModelScope.launch {
            try {
                budgetDao.updateBudget(budget)
                if (budget.category == "General") {
                    loadGeneralBudget()
                } else {
                    loadBudgets()
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun deleteBudget(budget: Budget) {
        viewModelScope.launch {
            try {
                budgetDao.deleteBudget(budget)
                if (budget.category == "General") {
                    loadGeneralBudget()
                } else {
                    loadBudgets()
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun suggestBudget(category: String) {
        viewModelScope.launch {
            try {
                budgetPredictor.suggestBudget(category).collect { amount ->
                    addBudget(category, amount, BudgetPeriod.MONTHLY)
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}