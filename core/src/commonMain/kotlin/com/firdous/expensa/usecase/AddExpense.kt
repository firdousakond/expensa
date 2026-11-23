package com.firdous.expensa.usecase

import com.firdous.expensa.model.Expense
import com.firdous.expensa.model.Trip
import com.firdous.expensa.repository.ExpenseRepository
import com.firdous.expensa.validation.ValidationResult
import com.firdous.expensa.validation.validateExpense

/**
 * Validates an expense against its trip and persists it only if valid.
 * Returns the validation result so the caller can surface errors.
 */
class AddExpense(
    private val expenses: ExpenseRepository,
) {
    suspend operator fun invoke(
        trip: Trip,
        expense: Expense,
    ): ValidationResult {
        val result = validateExpense(trip, expense)
        if (result is ValidationResult.Valid) {
            expenses.upsert(expense)
        }
        return result
    }
}
