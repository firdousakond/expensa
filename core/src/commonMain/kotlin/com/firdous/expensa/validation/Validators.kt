package com.firdous.expensa.validation

import com.firdous.expensa.model.Expense
import com.firdous.expensa.model.Trip

fun validateExpense(
    trip: Trip,
    expense: Expense,
): ValidationResult {
    val errors =
        buildList {
            if (expense.amount.amountMinor <= 0) {
                add(ValidationError.AmountNotPositive)
            }
            if (expense.amount.currency != trip.currency) {
                add(
                    ValidationError.CurrencyMismatch(
                        expected = trip.currency,
                        actual = expense.amount.currency,
                    ),
                )
            }
            if (expense.date < trip.startDate || expense.date > trip.endDate) {
                add(
                    ValidationError.DateOutsideTrip(
                        date = expense.date,
                        start = trip.startDate,
                        end = trip.endDate,
                    ),
                )
            }
        }
    return if (errors.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(errors)
}
