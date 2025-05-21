package com.example.walletai

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets")
    fun getAllBudgets(): Flow<List<Budget>>

    @Query("SELECT * FROM budgets WHERE category != 'General'")
    fun getAllCategoryBudgets(): Flow<List<Budget>>

    @Query("SELECT * FROM budgets WHERE category = 'General' LIMIT 1")
    fun getGeneralBudget(): Flow<Budget?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: Budget)

    @Update
    suspend fun updateBudget(budget: Budget)

    @Delete
    suspend fun deleteBudget(budget: Budget)

    @Query("SELECT * FROM budgets WHERE category = :category")
    fun getBudgetForCategory(category: String): Flow<Budget?>

    @Query("""
        SELECT b.category, b.amount, b.period
        FROM budgets b
        LEFT JOIN (
            SELECT category, SUM(amount) as spent 
            FROM expenses 
            WHERE date >= :startDate AND date <= :endDate
            GROUP BY category
        ) e ON b.category = e.category
        WHERE e.spent / b.amount > 0.8
    """)
    @RewriteQueriesToDropUnusedColumns
    fun getNearingBudgetLimits(startDate: Long, endDate: Long): Flow<List<Budget>>
}