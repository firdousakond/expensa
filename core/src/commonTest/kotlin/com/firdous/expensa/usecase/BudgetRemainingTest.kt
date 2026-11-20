package com.firdous.expensa.usecase

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

class BudgetRemainingTest {
    private val eur = Currency("EUR")

    @Test
    fun `returns budget minus total spent when under budget`() {
        val berlin = trip(TripId("berlin"), budget = Money(50000, eur)) // €500.00

        val expenses =
            listOf(
                expense(berlin.id, Money(1250, eur)), // €12.50
                expense(berlin.id, Money(250, eur)), // €2.50
            )

        assertEquals(Money(48500, eur), budgetRemaining(berlin, expenses)) // €485.00
    }

    @Test
    fun `goes negative when over budget`() {
        val berlin = trip(TripId("berlin"), budget = Money(1000, eur)) // €10.00

        val expenses = listOf(expense(berlin.id, Money(1500, eur))) // €15.00

        assertEquals(Money(-500, eur), budgetRemaining(berlin, expenses)) // -€5.00
    }

    private fun trip(
        id: TripId,
        budget: Money,
        currency: Currency = eur,
    ) = Trip(
        id = id,
        name = "Test Trip",
        startDate = LocalDate(2026, 1, 1),
        endDate = LocalDate(2026, 1, 7),
        currency = currency,
        budget = budget,
    )

    private fun expense(
        tripId: TripId,
        amount: Money,
    ) = Expense(
        id = ExpenseId("e-${amount.amountMinor}"),
        tripId = tripId,
        amount = amount,
        categoryId = CategoryId("food"),
        date = LocalDate(2026, 1, 1),
        note = null,
    )
}
