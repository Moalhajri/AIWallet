package com.example.walletai

sealed class InsightEvent {
    object RefreshData : InsightEvent()
    data class UpdateDateRange(val startDate: Long, val endDate: Long) : InsightEvent()
    data class SelectCategory(val category: String) : InsightEvent()
    object ClearError : InsightEvent()
    data class ExportData(val format: ExportFormat) : InsightEvent()
    object ToggleTrendView : InsightEvent()
}

enum class ExportFormat {
    PDF, CSV, EXCEL
}