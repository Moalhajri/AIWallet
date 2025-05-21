package com.example.walletai

data class InsightsState(
    val isLoading: Boolean = false,
    val categoryTotals: List<CategoryTotal> = emptyList(),
    val monthlyTotals: List<Double> = emptyList(),
    val budgetUtilization: Double = 0.0,
    val error: String? = null,
    val aiInsights: List<String> = emptyList()
)