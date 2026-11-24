package com.firdous.expensa.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.firdous.expensa.model.Category
import com.firdous.expensa.presentation.ExpenseListViewModel
import com.firdous.expensa.presentation.parseAmountToMinor
import com.firdous.expensa.validation.ValidationResult
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddExpenseDialog(
    viewModel: ExpenseListViewModel,
    categories: List<Category>,
    onDismiss: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    var amountText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull()?.id) }
    var dateText by remember { mutableStateOf(viewModel.defaultExpenseDate.toString()) }
    var note by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add expense") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    categories.forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category.id,
                            onClick = { selectedCategory = category.id },
                            label = { Text(category.name) },
                        )
                    }
                }
                OutlinedTextField(
                    value = dateText,
                    onValueChange = { dateText = it },
                    label = { Text("Date (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                error?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val amountMinor = parseAmountToMinor(amountText)
                val category = selectedCategory
                val date = runCatching { LocalDate.parse(dateText) }.getOrNull()
                when {
                    amountMinor == null -> error = "Enter a valid amount"
                    category == null -> error = "Pick a category"
                    date == null -> error = "Enter a valid date (YYYY-MM-DD)"
                    else -> {
                        error = null
                        scope.launch {
                            when (val result = viewModel.submitExpense(amountMinor, category, date, note)) {
                                is ValidationResult.Valid -> onDismiss()
                                is ValidationResult.Invalid ->
                                    error = result.errors.firstOrNull()?.message() ?: "Invalid expense"
                            }
                        }
                    }
                }
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
