package com.example.walletai

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getExpensesBetweenDates(startDate: Long, endDate: Long): Flow<List<Expense>>

    @Query("SELECT * FROM expenses ORDER BY date DESC LIMIT :limit")
    suspend fun getRecentExpenses(limit: Int): List<Expense>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("""
        SELECT 
            category,
            SUM(amount) as total,
            (SUM(amount) * 100.0 / NULLIF((SELECT SUM(amount) FROM expenses), 0)) as percentage
        FROM expenses 
        GROUP BY category
    """)
    fun getCategoryTotals(): Flow<List<CategoryTotal>>

    @Query("SELECT SUM(amount) FROM expenses")
    fun getTotalExpenses(): Flow<Double?>

    @Query("SELECT * FROM expenses WHERE category = :category")
    fun getExpensesForCategory(category: String): Flow<List<Expense>>

    @Query("""
        SELECT 
            category,
            COUNT(*) as count,
            SUM(amount) as total,
            AVG(amount) as average
        FROM expenses
        GROUP BY category
    """)
    fun getCategoryAnalytics(): Flow<List<CategoryAnalytics>>
}