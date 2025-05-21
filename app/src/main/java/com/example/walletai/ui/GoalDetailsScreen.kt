package com.example.walletai

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailsScreen(
    navController: NavController,
    viewModel: ActionPlanViewModel
) {
    val actionPlan by viewModel.actionPlan.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Goal Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Text("←")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            actionPlan?.let { plan ->
                Text("Action Plan", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))

                Text("Steps:", style = MaterialTheme.typography.titleMedium)
                plan.steps.forEachIndexed { index, step ->
                    Text("${index + 1}. $step")
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Timeline: ${plan.timeline}")

                Spacer(modifier = Modifier.height(16.dp))
                Text("Additional Information:", style = MaterialTheme.typography.titleMedium)
                plan.additionalInfo.forEach { (key, value) ->
                    Text("$key: $value")
                }
            } ?: Text("Loading action plan...")
        }
    }
}