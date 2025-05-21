package com.example.walletai

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    navController: NavController,
    viewModel: OnboardingViewModel
) {
    var initialBudget by remember { mutableStateOf("") }
    var selectedGoal by remember { mutableStateOf<FinancialGoal?>(null) }
    var showBankStatementUpload by remember { mutableStateOf(false) }
    val message by viewModel.message.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val bankStatementLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadBankStatement(it) }
    }

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Welcome to WalletAI",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = initialBudget,
            onValueChange = { initialBudget = it },
            label = { Text("Initial Monthly Budget") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "What's your main financial goal?",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        FinancialGoal.values().forEach { goal ->
            Button(
                onClick = { selectedGoal = goal },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedGoal == goal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(goal.description)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showBankStatementUpload = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Upload Bank Statement")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val budget = initialBudget.toDoubleOrNull() ?: 0.0
                if (budget > 0 && selectedGoal != null) {
                    scope.launch {
                        viewModel.setInitialBudgetAndGoal(budget, selectedGoal!!)
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Get Started")
        }
    }

    if (showBankStatementUpload) {
        AlertDialog(
            onDismissRequest = { showBankStatementUpload = false },
            title = { Text("Upload Bank Statement") },
            text = { Text("Choose a bank statement file to upload and analyze.") },
            confirmButton = {
                Button(onClick = {
                    bankStatementLauncher.launch("*/*")
                    showBankStatementUpload = false
                }) {
                    Text("Choose File")
                }
            },
            dismissButton = {
                Button(onClick = { showBankStatementUpload = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}