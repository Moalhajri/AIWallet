package com.example.walletai

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class PredictionUpdateWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val budgetPredictor: BudgetPredictor,
    private val budgetDao: BudgetDao
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = coroutineScope {
        try {
            val budgets = budgetDao.getAllBudgets().first()
            val categories = budgets.map { budget -> budget.category }

            for (category in categories) {
                withContext(Dispatchers.IO) {
                    val suggestedBudget = budgetPredictor.suggestBudget(category).first()
                    budgetDao.insertBudget(
                        Budget(
                            category = category,
                            amount = suggestedBudget,
                            period = BudgetPeriod.MONTHLY
                        )
                    )
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
    companion object {
        fun setupPeriodicWork(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresCharging(true)
                .build()

            val updateRequest = PeriodicWorkRequestBuilder<PredictionUpdateWorker>(1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "prediction_update",
                ExistingPeriodicWorkPolicy.UPDATE,
                updateRequest
            )
        }
    }
}