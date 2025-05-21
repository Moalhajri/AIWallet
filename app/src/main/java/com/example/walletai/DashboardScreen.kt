package com.example.walletai

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel
) {
    val expenses by viewModel.expenses.collectAsState()
    val categoryTotals by viewModel.categoryTotals.collectAsState()
    val totalBudget by viewModel.totalBudget.collectAsState()
    val totalExpenses by viewModel.totalExpenses.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("expenses") }) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            item {
                BudgetOverviewCard(totalBudget, totalExpenses)
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Text(
                    "Recent Expenses",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(expenses.take(5)) { expense ->
                DashboardExpenseItem(expense)
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "Category Totals",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(categoryTotals) { (category, total) ->
                CategoryTotalItem(category, total)
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = { navController.navigate("expenses") }) {
                        Text("View All Expenses")
                    }
                    Button(onClick = { navController.navigate("budget") }) {
                        Text("Manage Budget")
                    }
                }
            }
        }
    }
}

@Composable
fun BudgetOverviewCard(totalBudget: Double, totalExpenses: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Budget Overview", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Total Budget: $${String.format("%.2f", totalBudget)}")
            Text("Total Expenses: $${String.format("%.2f", totalExpenses)}")
            val remainingBudget = totalBudget - totalExpenses
            Text(
                "Remaining: $${String.format("%.2f", remainingBudget)}",
                color = if (remainingBudget >= 0) Color.Green else Color.Red
            )
            Spacer(modifier = Modifier.height(8.dp))
            val progress = if (totalBudget > 0) {
                (totalExpenses / totalBudget).coerceIn(0.0, 1.0)
            } else {
                1.0 // If totalBudget is 0, show full progress
            }
            LinearProgressIndicator(
                progress = progress.toFloat(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun DashboardExpenseItem(expense: Expense) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(expense.description, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(expense.category, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
        }
        Text(
            "$${String.format("%.2f", expense.amount)}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CategoryTotalItem(category: String, total: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(category, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        Text(
            "$${String.format("%.2f", total)}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}