package com.example.walletai

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

sealed class Screen(val route: String, val icon: ImageVector, val title: String) {
    object Onboarding : Screen("onboarding", Icons.Filled.Home, "Onboarding")
    object Dashboard : Screen("dashboard", Icons.Filled.Home, "Dashboard")
    object Budget : Screen("budget", Icons.Filled.List, "Budget")
    object Insights : Screen("insights", Icons.Filled.Info, "Insights")
    object Expenses : Screen("expenses", Icons.Filled.List, "Expenses")
    object GoalDetails : Screen("goal_details", Icons.Filled.Info, "Goal Details")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation(userPreferences: UserPreferences) {
    val navController = rememberNavController()
    val items = listOf(Screen.Dashboard, Screen.Budget, Screen.Insights, Screen.Expenses)

    val startDestination = remember {
        runBlocking { if (userPreferences.onboardingCompleted.first()) Screen.Dashboard.route else Screen.Onboarding.route }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Onboarding.route) {
                val viewModel = hiltViewModel<OnboardingViewModel>()
                OnboardingScreen(navController = navController, viewModel = viewModel)
            }
            composable(Screen.Dashboard.route) {
                val viewModel = hiltViewModel<DashboardViewModel>()
                DashboardScreen(navController = navController, viewModel = viewModel)
            }
            composable(Screen.Budget.route) {
                val viewModel = hiltViewModel<BudgetViewModel>()
                BudgetScreen(navController = navController, viewModel = viewModel)
            }
            composable(Screen.Insights.route) {
                val viewModel = hiltViewModel<InsightsViewModel>()
                InsightsScreen(navController = navController, viewModel = viewModel)
            }
            composable(Screen.Expenses.route) {
                val viewModel = hiltViewModel<ExpensesViewModel>()
                ExpensesScreen(navController = navController, viewModel = viewModel)
            }
            composable(Screen.GoalDetails.route) {
                val viewModel = hiltViewModel<ActionPlanViewModel>()
                GoalDetailsScreen(navController = navController, viewModel = viewModel)
            }
        }
    }
}