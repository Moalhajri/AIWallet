package com.example.walletai.ui.insights

// This file doesn't have a specific class, but contains sealed classes and a data class

sealed class InsightsIntent {
    object LoadInsights : InsightsIntent()
    object RefreshInsights : InsightsIntent()
}

data class InsightsViewState(
    val isLoading: Boolean = false,
    val categoryTotals: List<Pair<String, Double>> = emptyList(),
    val monthlyTotals: List<Double> = emptyList(),
    val error: String? = null
)

sealed class InsightsEffect {
    object DataWasLoaded : InsightsEffect()
    data class ShowError(val message: String) : InsightsEffect()
}