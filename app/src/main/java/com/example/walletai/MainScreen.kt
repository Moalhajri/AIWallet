package com.example.walletai

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MainScreen(viewModel: ExpensesViewModel = hiltViewModel()) {
    val expenses by viewModel.expenses.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Expenses", style = MaterialTheme.typography.headlineMedium)
        expenses.forEach { expense ->
            MainExpenseItem(expense)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Suggested Budgets", style = MaterialTheme.typography.headlineMedium)
        // Implement suggested budgets UI here when available

        Spacer(modifier = Modifier.height(16.dp))

        Text("Anomalies", style = MaterialTheme.typography.headlineMedium)
        // Implement anomalies UI here when available
    }
}

@Composable
fun MainExpenseItem(expense: Expense) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(expense.description)
        Text("$${expense.amount}")
    }
}

@Composable
fun BudgetItem(category: String, amount: Double) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(category)
        Text("$${String.format("%.2f", amount)}")
    }
}

@Composable
fun AnomalyItem(anomaly: Expense) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(anomaly.description, color = MaterialTheme.colorScheme.error)
        Text("$${anomaly.amount}", color = MaterialTheme.colorScheme.error)
    }
}