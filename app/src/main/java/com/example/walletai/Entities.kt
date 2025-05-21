package com.example.walletai

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Ignore

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val description: String,
    val amount: Double,
    val date: Long,
    val category: String,
    val tags: String = "", // Added tags field with default empty string
    val receiptPath: String = "" // Added receiptPath field with default empty string
)

data class CategoryTotal(
    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "total")
    val total: Double,

    @ColumnInfo(name = "percentage")
    val percentage: Double
) {
    val amount: Double get() = total
    val trend: String get() = "STABLE"
    val changePercentage: Double get() = 0.0
}

@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey val category: String,
    val amount: Double,
    val period: BudgetPeriod
)

enum class BudgetPeriod {
    WEEKLY, MONTHLY, YEARLY
}

data class CategoryAnalytics(
    val category: String,
    val count: Int,
    val total: Double,
    val average: Double
)

data class ExpenseTemplate(
    val description: String,
    val amount: Double,
    val category: String
)