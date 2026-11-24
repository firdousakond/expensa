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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.firdous.expensa.model.Category
import com.firdous.expensa.model.CategoryId
import com.firdous.expensa.presentation.parseAmountToMinor
import com.firdous.expensa.validation.ValidationResult
import expensa.app.shared.generated.resources.Res
import expensa.app.shared.generated.resources.action_cancel
import expensa.app.shared.generated.resources.action_save
import expensa.app.shared.generated.resources.add_expense_title
import expensa.app.shared.generated.resources.error_enter_valid_amount
import expensa.app.shared.generated.resources.error_enter_valid_date
import expensa.app.shared.generated.resources.error_invalid_expense
import expensa.app.shared.generated.resources.error_pick_category
import expensa.app.shared.generated.resources.field_amount
import expensa.app.shared.generated.resources.field_date
import expensa.app.shared.generated.resources.field_note
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

/** Submits the assembled form input; returns the domain validation result. */
typealias SubmitExpense = suspend (
    amountMinor: Long,
    categoryId: CategoryId,
    date: LocalDate,
    note: String?,
) -> ValidationResult

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddExpenseDialog(
    categories: List<Category>,
    defaultDate: LocalDate,
    onSubmit: SubmitExpense,
    onDismiss: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    // Resolved here (composable scope) so they can be used in the non-composable onClick.
    val invalidAmountMessage = stringResource(Res.string.error_enter_valid_amount)
    val pickCategoryMessage = stringResource(Res.string.error_pick_category)
    val invalidDateMessage = stringResource(Res.string.error_enter_valid_date)

    var amountText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull()?.id) }
    var dateText by remember { mutableStateOf(defaultDate.toString()) }
    var note by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.add_expense_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text(stringResource(Res.string.field_amount)) },
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
                    label = { Text(stringResource(Res.string.field_date)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text(stringResource(Res.string.field_note)) },
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
                    amountMinor == null -> error = invalidAmountMessage
                    category == null -> error = pickCategoryMessage
                    date == null -> error = invalidDateMessage
                    else -> {
                        error = null
                        scope.launch {
                            when (val result = onSubmit(amountMinor, category, date, note)) {
                                is ValidationResult.Valid -> onDismiss()
                                is ValidationResult.Invalid ->
                                    error =
                                        result.errors.firstOrNull()?.message()
                                            ?: getString(Res.string.error_invalid_expense)
                            }
                        }
                    }
                }
            }) { Text(stringResource(Res.string.action_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(Res.string.action_cancel)) }
        },
    )
}

@Preview
@Composable
fun AddExpenseDialogPreview() {
    MaterialTheme {
        AddExpenseDialog(
            categories =
                listOf(
                    Category(CategoryId("food"), "Food"),
                    Category(CategoryId("transport"), "Transport"),
                ),
            defaultDate = LocalDate(2026, 1, 1),
            onSubmit = { _, _, _, _ -> ValidationResult.Valid },
            onDismiss = {},
        )
    }
}
