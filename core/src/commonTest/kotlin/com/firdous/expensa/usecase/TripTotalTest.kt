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

class TripTotalTest {
    @Test
    fun `sums only the expenses that belong to the trip`() {
        val berlin = trip(TripId("berlin"))
        val paris = TripId("paris")

        val expenses =
            listOf(
                expense(berlin.id, Money(1250, Currency("EUR"))), // €12.50
                expense(berlin.id, Money(250, Currency("EUR"))), // €2.50
                expense(paris, Money(900, Currency("EUR"))), // different trip — excluded
            )

        assertEquals(Money(1500, Currency("EUR")), tripTotal(berlin, expenses))
    }

    @Test
    fun `returns zero in the trip currency when there are no expenses`() {
        val berlin = trip(TripId("berlin"))

        assertEquals(Money(0, Currency("EUR")), tripTotal(berlin, emptyList()))
    }

    private fun trip(
        id: TripId,
        currency: Currency = Currency("EUR"),
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
