package com.example.walletai.ui.insights

interface InsightsEventHandler {
    fun onEvent(event: InsightsEvent)
}

sealed class InsightsEvent {
    object Refresh : InsightsEvent()
    data class TimeRangeSelected(val timeRange: TimeRange) : InsightsEvent()
    data class CategorySelected(val category: String) : InsightsEvent()
    object DismissError : InsightsEvent()
}

enum class TimeRange {
    WEEK,
    MONTH,
    QUARTER,
    YEAR
}