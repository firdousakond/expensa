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

class PerCategoryBreakdownTest {
    private val eur = Currency("EUR")

    @Test
    fun `groups expenses by category and sums each`() {
        val berlin = trip(TripId("berlin"))
        val food = CategoryId("food")
        val transport = CategoryId("transport")

        val expenses =
            listOf(
                expense(berlin.id, food, Money(1250, eur)), // food 12.50
                expense(berlin.id, food, Money(250, eur)), // food  2.50
                expense(berlin.id, transport, Money(900, eur)), // transport 9.00
                expense(TripId("paris"), food, Money(5000, eur)), // other trip — excluded
            )

        val expected =
            mapOf(
                food to Money(1500, eur),
                transport to Money(900, eur),
            )
        assertEquals(expected, perCategoryBreakdown(berlin, expenses))
    }

    @Test
    fun `returns an empty map when there are no expenses`() {
        val berlin = trip(TripId("berlin"))
        assertEquals(emptyMap(), perCategoryBreakdown(berlin, emptyList()))
    }

    private fun trip(
        id: TripId,
        currency: Currency = eur,
    ) = Trip(
        id = id,
        name = "Test Trip",
        startDate = LocalDate(2026, 1, 1),
        endDate = LocalDate(2026, 1, 7),
        currency = currency,
        budget = Money(0, currency),
    )

    private fun expense(
        tripId: TripId,
        categoryId: CategoryId,
        amount: Money,
    ) = Expense(
        id = ExpenseId("e-${categoryId.value}-${amount.amountMinor}"),
        tripId = tripId,
        amount = amount,
        categoryId = categoryId,
        date = LocalDate(2026, 1, 1),
        note = null,
    )
}
