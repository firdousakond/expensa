package com.firdous.expensa.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.firdous.expensa.model.Expense
import com.firdous.expensa.presentation.ExpenseListUiState
import com.firdous.expensa.presentation.ExpenseListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListScreen(viewModel: ExpenseListViewModel) {
    val state by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddExpenseDialog(
            viewModel = viewModel,
            categories = state.categories,
            onDismiss = { showAddDialog = false },
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Expenses") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Text("+", style = MaterialTheme.typography.headlineMedium)
            }
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SummaryHeader(state)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.expenses) { expense -> ExpenseRow(expense) }
            }
        }
    }
}

@Composable
private fun SummaryHeader(state: ExpenseListUiState) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Total: ${state.summary?.total?.format() ?: "—"}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Budget left: ${state.summary?.remaining?.format() ?: "—"}",
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun ExpenseRow(expense: Expense) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = expense.amount.format(), fontWeight = FontWeight.SemiBold)
            Text(
                text = "${expense.categoryId.value} · ${expense.date}",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
