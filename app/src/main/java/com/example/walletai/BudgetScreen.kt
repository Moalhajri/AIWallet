package com.example.walletai

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    navController: NavController,
    viewModel: BudgetViewModel
) {
    val budgets by viewModel.budgets.collectAsState()
    val generalBudget by viewModel.generalBudget.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var isGeneralBudget by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Budget") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true; isGeneralBudget = false }) {
                Icon(Icons.Default.Add, contentDescription = "Add Budget")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            item {
                if (generalBudget != null) {
                    BudgetItem(budget = generalBudget!!, isGeneral = true)
                } else {
                    Button(onClick = {
                        showAddDialog = true
                        isGeneralBudget = true
                    }) {
                        Text("Set General Budget")
                    }
                }
            }
            items(budgets) { budget ->
                BudgetItem(budget = budget, isGeneral = false)
            }
        }
    }

    if (showAddDialog) {
        BudgetDialog(
            onDismiss = { showAddDialog = false },
            onSave = { category, amount, period ->
                viewModel.addBudget(category, amount, period, isGeneralBudget)
                showAddDialog = false
            },
            isGeneralBudget = isGeneralBudget,
            initialAmount = if (isGeneralBudget) generalBudget?.amount?.toString() ?: "" else ""
        )
    }
}

@Composable
fun BudgetItem(budget: Budget, isGeneral: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = if (isGeneral) "General Budget" else budget.category, style = MaterialTheme.typography.titleMedium)
            Text(text = "Amount: $${budget.amount}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Period: ${budget.period}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetDialog(
    onDismiss: () -> Unit,
    onSave: (String, Double, BudgetPeriod) -> Unit,
    isGeneralBudget: Boolean,
    initialAmount: String = ""
) {
    var category by remember { mutableStateOf(if (isGeneralBudget) "General" else "") }
    var amount by remember { mutableStateOf(initialAmount) }
    var period by remember { mutableStateOf(BudgetPeriod.MONTHLY) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isGeneralBudget) "Set General Budget" else "Add Budget") },
        text = {
            Column {
                if (!isGeneralBudget) {
                    TextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                TextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = {}
                ) {
                    TextField(
                        value = period.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Period") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = false,
                        onDismissRequest = {}
                    ) {
                        BudgetPeriod.values().forEach { budgetPeriod ->
                            DropdownMenuItem(
                                text = { Text(budgetPeriod.name) },
                                onClick = { period = budgetPeriod }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val amountValue = amount.toDoubleOrNull() ?: 0.0
                onSave(if (isGeneralBudget) "General" else category, amountValue, period)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}