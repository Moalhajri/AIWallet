package com.example.walletai

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class BudgetPredictor @Inject constructor(
    private val expenseDao: ExpenseDao
) {
    fun suggestBudget(category: String): Flow<Double> {
        return expenseDao.getExpensesForCategory(category)
            .map { expenses ->
                val average = if (expenses.isNotEmpty()) {
                    expenses.map { it.amount }.average()
                } else {
                    0.0
                }
                // Simple suggestion: 10% more than the average expense
                average * 1.1
            }
    }

    fun predictExpenseForCategory(category: String): Flow<Double> {
        return expenseDao.getExpensesForCategory(category)
            .map { expenses ->
                if (expenses.isNotEmpty()) {
                    expenses.map { it.amount }.average()
                } else {
                    0.0
                }
            }
    }
}