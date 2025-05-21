package com.example.walletai

import kotlinx.coroutines.flow.first
import kotlin.math.min

class SavingsPlanner(private val expenseDao: ExpenseDao) {
    suspend fun createSavingsPlan(targetAmount: Double, targetDate: Long): SavingsPlan {
        val expenses = expenseDao.getAllExpenses().first()
        val monthlyExpenses = expenses.map { it.amount }.average()
        val monthlyDisposableIncome = 2000.0 - monthlyExpenses // Assuming a fixed income of 2000

        val monthsUntilTarget = (targetDate - System.currentTimeMillis()) / (30L * 24 * 60 * 60 * 1000)
        val recommendedMonthlySaving = targetAmount / monthsUntilTarget

        val feasibleMonthlySaving = min(recommendedMonthlySaving, monthlyDisposableIncome * 0.5)

        return SavingsPlan(
            targetAmount = targetAmount,
            targetDate = targetDate,
            recommendedMonthlySaving = feasibleMonthlySaving,
            estimatedCompletionDate = if (feasibleMonthlySaving < recommendedMonthlySaving)
                System.currentTimeMillis() + ((targetAmount / feasibleMonthlySaving) * 30L * 24 * 60 * 60 * 1000).toLong()
            else
                targetDate
        )
    }
}

data class SavingsPlan(
    val targetAmount: Double,
    val targetDate: Long,
    val recommendedMonthlySaving: Double,
    val estimatedCompletionDate: Long
)