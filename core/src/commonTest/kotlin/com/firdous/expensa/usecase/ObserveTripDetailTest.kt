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

class ObserveTripDetailTest {
    private val eur = Currency("EUR")
    private val berlinId = TripId("berlin")

    @Test
    fun `emits expenses and a consistent summary`() =
        runTest {
            val trips = FakeTripRepository()
            val expenses = FakeExpenseRepository()
            trips.upsert(trip())
            expenses.upsert(expense("e1", CategoryId("food"), Money(1250, eur)))
            expenses.upsert(expense("e2", CategoryId("transport"), Money(900, eur)))

            val observeTripDetail = ObserveTripDetail(trips, expenses)

            observeTripDetail(berlinId).test {
                val detail = awaitItem()
                assertEquals(2, detail?.expenses?.size)
                assertEquals(Money(2150, eur), detail?.summary?.total)
                assertEquals(Money(47850, eur), detail?.summary?.remaining)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `list and summary update together when an expense is added`() =
        runTest {
            val trips = FakeTripRepository()
            val expenses = FakeExpenseRepository()
            trips.upsert(trip())
            val observeTripDetail = ObserveTripDetail(trips, expenses)

            observeTripDetail(berlinId).test {
                val initial = awaitItem()
                assertEquals(0, initial?.expenses?.size)
                assertEquals(Money(0, eur), initial?.summary?.total)

                expenses.upsert(expense("e1", CategoryId("food"), Money(1250, eur)))

                val updated = awaitItem()
                assertEquals(1, updated?.expenses?.size)
                assertEquals(Money(1250, eur), updated?.summary?.total) // consistent in one emission
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `emits null when the trip does not exist`() =
        runTest {
            val observeTripDetail = ObserveTripDetail(FakeTripRepository(), FakeExpenseRepository())

            observeTripDetail(berlinId).test {
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
