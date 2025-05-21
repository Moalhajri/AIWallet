package com.example.walletai

import java.time.YearMonth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIInsightsGenerator @Inject constructor() {
    fun generateInsights(
        categoryTotals: List<Pair<String, Double>>,
        monthlyTotals: List<Pair<YearMonth, Double>>,
        budgetUtilization: Double
    ): List<String> {
        val insights = mutableListOf<String>()

        // Add insight about highest spending category
        val highestSpendingCategory = categoryTotals.maxByOrNull { it.second }
        highestSpendingCategory?.let {
            insights.add("Your highest spending category is ${it.first} with $${String.format("%.2f", it.second)}.")
        }

        // Add insight about spending trend
        val spendingTrend = calculateSpendingTrend(monthlyTotals)
        insights.add("Your spending trend over the last few months is $spendingTrend.")

        // Add insight about budget utilization
        val budgetInsight = when {
            budgetUtilization > 1.0 -> "You're over budget. Consider cutting back on expenses."
            budgetUtilization > 0.9 -> "You're close to your budget limit. Be cautious with further spending."
            budgetUtilization < 0.5 -> "You're well under budget. Consider saving or investing the extra money."
            else -> "Your budget utilization is healthy. Keep up the good work!"
        }
        insights.add(budgetInsight)

        return insights
    }

    private fun calculateSpendingTrend(monthlyTotals: List<Pair<YearMonth, Double>>): String {
        if (monthlyTotals.size < 2) return "insufficient data"

        val sortedTotals = monthlyTotals.sortedBy { it.first }
        val firstHalf = sortedTotals.take(sortedTotals.size / 2)
        val secondHalf = sortedTotals.takeLast(sortedTotals.size / 2)

        val firstHalfAvg = firstHalf.map { it.second }.average()
        val secondHalfAvg = secondHalf.map { it.second }.average()

        return when {
            secondHalfAvg > firstHalfAvg * 1.1 -> "increasing"
            secondHalfAvg < firstHalfAvg * 0.9 -> "decreasing"
            else -> "stable"
        }
    }
}