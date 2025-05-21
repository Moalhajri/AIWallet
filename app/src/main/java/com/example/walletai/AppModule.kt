package com.example.walletai

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideExpenseDatabase(
        @ApplicationContext context: Context
    ): ExpenseDatabase = ExpenseDatabase.getDatabase(context)

    @Provides
    fun provideExpenseDao(database: ExpenseDatabase): ExpenseDao = database.expenseDao()

    @Provides
    fun provideBudgetDao(database: ExpenseDatabase): BudgetDao = database.budgetDao()

    @Singleton
    @Provides
    fun provideBudgetPredictor(expenseDao: ExpenseDao): BudgetPredictor {
        return BudgetPredictor(expenseDao)
    }

    @Singleton
    @Provides
    fun provideExpenseCategorizer(): ExpenseCategorizer {
        return ExpenseCategorizer()
    }

    @Singleton
    @Provides
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }
}