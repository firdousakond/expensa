package com.firdous.expensa.usecase

import app.cash.turbine.test
import com.firdous.expensa.model.CategoryId
import com.firdous.expensa.model.Currency
import com.firdous.expensa.model.Expense
import com.firdous.expensa.model.ExpenseId
import com.firdous.expensa.model.Money
import com.firdous.expensa.model.Trip
import com.firdous.expensa.model.TripId
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ObserveTripSummaryTest {
    private val eur = Currency("EUR")
    private val berlinId = TripId("berlin")

    @Test
    fun `emits summary with total, per-category and remaining`() =
        runTest {
            val trips = FakeTripRepository()
            val expenses = FakeExpenseRepository()
            trips.upsert(trip())
            expenses.upsert(expense("e1", CategoryId("food"), Money(1250, eur)))
            expenses.upsert(expense("e2", CategoryId("transport"), Money(900, eur)))

            val observeTripSummary = ObserveTripSummary(trips, expenses)

            observeTripSummary(berlinId).test {
                val summary = awaitItem()
                assertEquals(Money(2150, eur), summary?.total)
                assertEquals(Money(1250, eur), summary?.perCategory?.get(CategoryId("food")))
                assertEquals(Money(900, eur), summary?.perCategory?.get(CategoryId("transport")))
                assertEquals(Money(47850, eur), summary?.remaining) // 50000 - 2150
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `updates when an expense is added`() =
        runTest {
            val trips = FakeTripRepository()
            val expenses = FakeExpenseRepository()
            trips.upsert(trip())
            val observeTripSummary = ObserveTripSummary(trips, expenses)

            observeTripSummary(berlinId).test {
                assertEquals(Money(0, eur), awaitItem()?.total) // no expenses yet
                expenses.upsert(expense("e1", CategoryId("food"), Money(1250, eur)))
                assertEquals(Money(1250, eur), awaitItem()?.total)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `emits null when the trip does not exist`() =
        runTest {
            val observeTripSummary = ObserveTripSummary(FakeTripRepository(), FakeExpenseRepository())

            observeTripSummary(berlinId).test {
                assertNull(awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    private fun trip() =
        Trip(
            id = berlinId,
            name = "Test Trip",
            startDate = LocalDate(2026, 1, 1),
            endDate = LocalDate(2026, 1, 7),
            currency = eur,
            budget = Money(50000, eur),
        )

    private fun expense(
        id: String,
        categoryId: CategoryId,
        amount: Money,
    ) = Expense(
        id = ExpenseId(id),
        tripId = berlinId,
        amount = amount,
        categoryId = categoryId,
        date = LocalDate(2026, 1, 3),
        note = null,
    )
}
