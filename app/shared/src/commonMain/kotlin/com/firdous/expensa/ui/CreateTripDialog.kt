package com.firdous.expensa.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
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
import com.firdous.expensa.presentation.parseAmountToMinor
import com.firdous.expensa.validation.ValidationResult
import expensa.app.shared.generated.resources.Res
import expensa.app.shared.generated.resources.action_cancel
import expensa.app.shared.generated.resources.action_create
import expensa.app.shared.generated.resources.create_trip_title
import expensa.app.shared.generated.resources.error_enter_name
import expensa.app.shared.generated.resources.error_enter_valid_budget
import expensa.app.shared.generated.resources.error_enter_valid_currency
import expensa.app.shared.generated.resources.error_enter_valid_date
import expensa.app.shared.generated.resources.field_budget
import expensa.app.shared.generated.resources.field_currency
import expensa.app.shared.generated.resources.field_end_date
import expensa.app.shared.generated.resources.field_name
import expensa.app.shared.generated.resources.field_start_date
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

/** Assembles form input into a trip; returns the domain validation result. */
typealias SubmitTrip = suspend (
    name: String,
    startDate: LocalDate,
    endDate: LocalDate,
    currencyCode: String,
    budgetMinor: Long,
) -> ValidationResult

@Composable
fun CreateTripDialog(
    onSubmit: SubmitTrip,
    onDismiss: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    val nameError = stringResource(Res.string.error_enter_name)
    val dateError = stringResource(Res.string.error_enter_valid_date)
    val currencyError = stringResource(Res.string.error_enter_valid_currency)
    val budgetError = stringResource(Res.string.error_enter_valid_budget)

    var name by remember { mutableStateOf("") }
    var startText by remember { mutableStateOf("") }
    var endText by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("EUR") }
    var budgetText by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.create_trip_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(Res.string.field_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = startText,
                    onValueChange = { startText = it },
                    label = { Text(stringResource(Res.string.field_start_date)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = endText,
                    onValueChange = { endText = it },
                    label = { Text(stringResource(Res.string.field_end_date)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = currency,
                    onValueChange = { currency = it },
                    label = { Text(stringResource(Res.string.field_currency)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = budgetText,
                    onValueChange = { budgetText = it },
                    label = { Text(stringResource(Res.string.field_budget)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                )
                error?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val trimmedName = name.trim()
                val start = runCatching { LocalDate.parse(startText) }.getOrNull()
                val end = runCatching { LocalDate.parse(endText) }.getOrNull()
                val budget = parseAmountToMinor(budgetText)
                when {
                    trimmedName.isEmpty() -> error = nameError
                    start == null || end == null -> error = dateError
                    currency.isBlank() -> error = currencyError
                    budget == null -> error = budgetError
                    else -> {
                        error = null
                        scope.launch {
                            when (val result = onSubmit(trimmedName, start, end, currency.trim(), budget)) {
                                is ValidationResult.Valid -> onDismiss()
                                is ValidationResult.Invalid ->
                                    error = result.errors.firstOrNull()?.message() ?: dateError
                            }
                        }
                    }
                }
            }) { Text(stringResource(Res.string.action_create)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(Res.string.action_cancel)) }
        },
    )
}
