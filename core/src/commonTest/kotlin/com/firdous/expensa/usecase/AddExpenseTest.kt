package com.firdous.expensa.usecase

import com.firdous.expensa.model.CategoryId
import com.firdous.expensa.model.Currency
import com.firdous.expensa.model.Expense
import com.firdous.expensa.model.ExpenseId
import com.firdous.expensa.model.Money
import com.firdous.expensa.model.Trip
import com.firdous.expensa.model.TripId
import com.firdous.expensa.validation.ValidationResult
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class AddExpenseTest {
    private val eur = Currency("EUR")

    @Test
    fun `valid expense is persisted and returns Valid`() =
        runTest {
            val repo = FakeExpenseRepository()
            val addExpense = AddExpense(repo)
            val trip = trip()
            val expense = expense(amount = Money(1250, eur))

            val result = addExpense(trip, expense)

            assertEquals(ValidationResult.Valid, result)
            assertEquals(expense, repo.getExpense(expense.id))
        }

    @Test
    fun `invalid expense is not persisted and returns Invalid`() =
        runTest {
            val repo = FakeExpenseRepository()
            val addExpense = AddExpense(repo)
            val trip = trip() // EUR trip
            val expense = expense(amount = Money(1250, Currency("USD"))) // currency mismatch

            val result = addExpense(trip, expense)

            assertIs<ValidationResult.Invalid>(result)
            assertNull(repo.getExpense(expense.id))
        }

    private fun trip() =
        Trip(
            id = TripId("berlin"),
            name = "Test Trip",
            startDate = LocalDate(2026, 1, 1),
            endDate = LocalDate(2026, 1, 7),
            currency = eur,
            budget = Money(50000, eur),
        )

    private fun expense(amount: Money) =
        Expense(
            id = ExpenseId("e1"),
            tripId = TripId("berlin"),
            amount = amount,
            categoryId = CategoryId("food"),
            date = LocalDate(2026, 1, 3),
            note = null,
        )
}
