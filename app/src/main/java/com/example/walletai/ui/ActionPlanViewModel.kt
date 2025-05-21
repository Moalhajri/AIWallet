package com.example.walletai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActionPlanViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val expenseDao: ExpenseDao,
    private val budgetDao: BudgetDao
) : ViewModel() {

    private val _actionPlan = MutableStateFlow<ActionPlan?>(null)
    val actionPlan: StateFlow<ActionPlan?> = _actionPlan

    init {
        viewModelScope.launch {
            userPreferences.financialGoal.collect { goal ->
                goal?.let { generateActionPlan(it) }
            }
        }
    }

    private suspend fun generateActionPlan(goal: FinancialGoal) {
        val plan = when (goal) {
            FinancialGoal.SAVE_MONEY -> generateSavingsPlan()
            FinancialGoal.PAY_OFF_DEBT -> generateDebtPayoffPlan()
            FinancialGoal.INVEST -> generateInvestmentPlan()
            FinancialGoal.BUDGET_BETTER -> generateBudgetPlan()
            FinancialGoal.INCREASE_INCOME -> generateIncomeIncreasePlan()
        }
        _actionPlan.value = plan
    }

    private suspend fun generateSavingsPlan(): ActionPlan {
        // Implement savings plan generation logic
        return ActionPlan(
            steps = listOf(
                "Set a savings goal",
                "Create a budget",
                "Cut unnecessary expenses",
                "Set up automatic transfers to savings account"
            ),
            timeline = "6 months",
            additionalInfo = mapOf(
                "Target Amount" to "$5000",
                "Monthly Savings" to "$833"
            )
        )
    }

    private suspend fun generateDebtPayoffPlan(): ActionPlan {
        // Implement debt payoff plan generation logic
        return ActionPlan(
            steps = listOf(
                "List all debts",
                "Prioritize high-interest debts",
                "Create a debt snowball/avalanche plan",
                "Explore debt consolidation options"
            ),
            timeline = "12 months",
            additionalInfo = mapOf(
                "Total Debt" to "$10000",
                "Monthly Payment" to "$1000"
            )
        )
    }

    private suspend fun generateInvestmentPlan(): ActionPlan {
        // Implement investment plan generation logic
        return ActionPlan(
            steps = listOf(
                "Assess risk tolerance",
                "Research investment options",
                "Open an investment account",
                "Start with low-cost index funds"
            ),
            timeline = "Long-term",
            additionalInfo = mapOf(
                "Initial Investment" to "$1000",
                "Monthly Contribution" to "$200"
            )
        )
    }

    private suspend fun generateBudgetPlan(): ActionPlan {
        // Implement budget plan generation logic
        return ActionPlan(
            steps = listOf(
                "Track all expenses for a month",
                "Categorize expenses",
                "Set spending limits for each category",
                "Review and adjust budget regularly"
            ),
            timeline = "1 month",
            additionalInfo = mapOf(
                "Current Monthly Expenses" to "$3000",
                "Target Monthly Expenses" to "$2500"
            )
        )
    }

    private suspend fun generateIncomeIncreasePlan(): ActionPlan {
        // Implement income increase plan generation logic
        return ActionPlan(
            steps = listOf(
                "Identify skills for improvement",
                "Research job market trends",
                "Explore freelance opportunities",
                "Negotiate a raise or promotion"
            ),
            timeline = "3-6 months",
            additionalInfo = mapOf(
                "Current Income" to "$4000/month",
                "Target Income" to "$5000/month"
            )
        )
    }
}

data class ActionPlan(
    val steps: List<String>,
    val timeline: String,
    val additionalInfo: Map<String, String>
)