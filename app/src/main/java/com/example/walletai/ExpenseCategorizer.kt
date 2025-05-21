package com.example.walletai

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseCategorizer @Inject constructor() {
    private val categories = listOf("Food", "Transportation", "Entertainment", "Utilities", "Shopping")

    fun categorizeExpense(description: String): String {
        // Simple categorization based on keywords
        return when {
            description.contains("grocery", ignoreCase = true) -> "Food"
            description.contains("restaurant", ignoreCase = true) -> "Food"
            description.contains("gas", ignoreCase = true) -> "Transportation"
            description.contains("uber", ignoreCase = true) -> "Transportation"
            description.contains("movie", ignoreCase = true) -> "Entertainment"
            description.contains("electric", ignoreCase = true) -> "Utilities"
            description.contains("water", ignoreCase = true) -> "Utilities"
            description.contains("amazon", ignoreCase = true) -> "Shopping"
            else -> "Other"
        }
    }

    fun detectAnomaly(expense: Expense, userAverages: Map<String, Double>): Boolean {
        val categoryAverage = userAverages[expense.category] ?: return false
        return expense.amount > categoryAverage * 2 // Flag if more than double the average
    }
}