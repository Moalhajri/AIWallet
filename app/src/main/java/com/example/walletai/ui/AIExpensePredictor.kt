package com.example.walletai

import android.content.Context
import kotlin.math.exp

class AIExpensePredictor(private val context: Context) {

    fun predictNextExpense(recentExpenses: List<Expense>): ExpenseTemplate {
        if (recentExpenses.isEmpty()) {
            return ExpenseTemplate("Default Prediction", 0.0, "Uncategorized")
        }

        val predictedAmount = predictAmount(recentExpenses)
        val predictedCategory = predictCategory(recentExpenses)

        return ExpenseTemplate(
            description = "AI Predicted Expense",
            amount = predictedAmount,
            category = predictedCategory
        )
    }

    private fun predictAmount(recentExpenses: List<Expense>): Double {
        val amounts = recentExpenses.map { it.amount }
        return exponentialMovingAverage(amounts)
    }

    private fun predictCategory(recentExpenses: List<Expense>): String {
        return recentExpenses.groupBy { it.category }
            .maxByOrNull { it.value.size }?.key ?: "Uncategorized"
    }

    private fun exponentialMovingAverage(values: List<Double>, alpha: Double = 0.3): Double {
        if (values.isEmpty()) return 0.0
        var ema = values.first()
        for (i in 1 until values.size) {
            ema = alpha * values[i] + (1 - alpha) * ema
        }
        return ema
    }
}