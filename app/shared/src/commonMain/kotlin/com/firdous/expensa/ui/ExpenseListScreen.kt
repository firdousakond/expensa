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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.firdous.expensa.model.Category
import com.firdous.expensa.model.CategoryId
import com.firdous.expensa.model.Currency
import com.firdous.expensa.model.Expense
import com.firdous.expensa.model.ExpenseId
import com.firdous.expensa.model.Money
import com.firdous.expensa.model.TripId
import com.firdous.expensa.model.TripSummary
import com.firdous.expensa.presentation.ExpenseListUiState
import com.firdous.expensa.presentation.ExpenseListViewModel
import expensa.app.shared.generated.resources.Res
import expensa.app.shared.generated.resources.app_bar_expenses
import expensa.app.shared.generated.resources.summary_budget_left
import expensa.app.shared.generated.resources.summary_total
import expensa.app.shared.generated.resources.value_none
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

/** Stateful entry point: binds the ViewModel to the stateless content. */
@Composable
fun ExpenseListScreen(viewModel: ExpenseListViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddExpenseDialog(
            categories = state.categories,
            defaultDate = viewModel.defaultExpenseDate,
            onSubmit = viewModel::submitExpense,
            onDismiss = { showAddDialog = false },
        )
    }

    ExpenseListContent(state = state, onAddClick = { showAddDialog = true })
}

/** Stateless, previewable content. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListContent(
    state: ExpenseListUiState,
    onAddClick: () -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(Res.string.app_bar_expenses)) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
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
            SummaryHeader(state.summary)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.expenses, key = { it.id.value }) { expense -> ExpenseRow(expense) }
            }
        }
    }
}

@Composable
private fun SummaryHeader(summary: TripSummary?) {
    val none = stringResource(Res.string.value_none)
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(Res.string.summary_total, summary?.total?.format() ?: none),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(Res.string.summary_budget_left, summary?.remaining?.format() ?: none),
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

@Preview
@Composable
fun ExpenseListContentPreview() {
    val eur = Currency("EUR")
    val expenses =
        listOf(
            previewExpense("e1", "food", Money(1250, eur)),
            previewExpense("e2", "transport", Money(900, eur)),
        )
    MaterialTheme {
        ExpenseListContent(
            state =
                ExpenseListUiState(
                    expenses = expenses,
                    summary =
                        TripSummary(
                            total = Money(2150, eur),
                            perCategory = emptyMap(),
                            remaining = Money(47850, eur),
                        ),
                    categories = listOf(Category(CategoryId("food"), "Food")),
                ),
            onAddClick = {},
        )
    }
}

@Preview
@Composable
fun ExpenseListContentEmptyPreview() {
    val eur = Currency("EUR")
    MaterialTheme {
        ExpenseListContent(
            state =
                ExpenseListUiState(
                    expenses = emptyList(),
                    summary =
                        TripSummary(
                            total = Money(0, eur),
                            perCategory = emptyMap(),
                            remaining = Money(50000, eur),
                        ),
                ),
            onAddClick = {},
        )
    }
}

private fun previewExpense(
    id: String,
    category: String,
    amount: Money,
) = Expense(
    id = ExpenseId(id),
    tripId = TripId("berlin"),
    amount = amount,
    categoryId = CategoryId(category),
    date = LocalDate(2026, 1, 3),
    note = null,
)
