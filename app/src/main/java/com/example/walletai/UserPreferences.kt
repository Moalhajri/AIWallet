package com.example.walletai

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferences @Inject constructor(@ApplicationContext private val context: Context) {

    private object PreferencesKeys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val FINANCIAL_GOAL = stringPreferencesKey("financial_goal")
    }

    val onboardingCompleted: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
        }

    suspend fun markOnboardingCompleted() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] = true
        }
    }

    suspend fun saveFinancialGoal(goal: FinancialGoal) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FINANCIAL_GOAL] = goal.name
        }
    }

    val financialGoal: Flow<FinancialGoal?> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.FINANCIAL_GOAL]?.let { FinancialGoal.valueOf(it) }
        }
}