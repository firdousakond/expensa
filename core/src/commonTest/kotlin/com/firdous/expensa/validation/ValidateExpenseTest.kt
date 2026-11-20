package com.firdous.expensa.validation

import com.firdous.expensa.model.CategoryId
import com.firdous.expensa.model.Currency
import com.firdous.expensa.model.Expense
import com.firdous.expensa.model.ExpenseId
import com.firdous.expensa.model.Money
import com.firdous.expensa.model.Trip
import com.firdous.expensa.model.TripId
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ValidateExpenseTest {
    private val eur = Currency("EUR")

    @Test
    fun `a well-formed expense is valid`() {
        val trip = trip()
        val expense = expense(amount = Money(1250, eur))

        assertEquals(ValidationResult.Valid, validateExpense(trip, expense))
    }

    @Test
    fun `reports AmountNotPositive when the amount is zero`() {
        val trip = trip()
        val expense = expense(amount = Money(0, eur))

        val result = assertIs<ValidationResult.Invalid>(validateExpense(trip, expense))
        assertTrue(ValidationError.AmountNotPositive in result.errors)
    }

    @Test
    fun `reports CurrencyMismatch when expense currency differs from the trip`() {
        val trip = trip() // EUR trip
        val expense = expense(amount = Money(1250, Currency("USD")))

        val result = assertIs<ValidationResult.Invalid>(validateExpense(trip, expense))
        assertTrue(
            ValidationError.CurrencyMismatch(expected = eur, actual = Currency("USD")) in result.errors,
        )
    }

    @Test
    fun `reports DateOutsideTrip when the expense date is outside the trip range`() {
        val trip = trip() // 2026-01-01 .. 2026-01-07
        val expense = expense(amount = Money(1250, eur), date = LocalDate(2026, 2, 1)) // after trip

        val result = assertIs<ValidationResult.Invalid>(validateExpense(trip, expense))
        assertTrue(
            ValidationError.DateOutsideTrip(
                date = LocalDate(2026, 2, 1),
                start = LocalDate(2026, 1, 1),
                end = LocalDate(2026, 1, 7),
            ) in result.errors,
        )
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

    private fun expense(
        amount: Money,
        date: LocalDate = LocalDate(2026, 1, 3),
    ) = Expense(
        id = ExpenseId("e1"),
        tripId = TripId("berlin"),
        amount = amount,
        categoryId = CategoryId("food"),
        date = date,
        note = null,
    )
}
