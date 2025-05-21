package com.example.walletai

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletAIApp() {
    val navController = rememberNavController()

    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("dashboard") {
                val viewModel: DashboardViewModel = hiltViewModel()
                DashboardScreen(navController, viewModel)
            }
            composable("expenses") {
                val viewModel: ExpensesViewModel = hiltViewModel()
                ExpensesScreen(navController, viewModel)
            }
            composable("budget") {
                val viewModel: BudgetViewModel = hiltViewModel()
                BudgetScreen(navController, viewModel)
            }
            composable("insights") {
                val viewModel: InsightsViewModel = hiltViewModel()
                InsightsScreen(navController, viewModel)
            }
        }
    }
}