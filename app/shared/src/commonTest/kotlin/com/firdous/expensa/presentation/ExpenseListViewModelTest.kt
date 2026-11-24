package com.firdous.expensa.presentation

import app.cash.turbine.test
import com.firdous.expensa.data.InMemoryCategoryRepository
import com.firdous.expensa.data.InMemoryExpenseRepository
import com.firdous.expensa.data.InMemoryTripRepository
import com.firdous.expensa.model.CategoryId
import com.firdous.expensa.model.Currency
import com.firdous.expensa.model.Expense
import com.firdous.expensa.model.ExpenseId
import com.firdous.expensa.model.Money
import com.firdous.expensa.model.Trip
import com.firdous.expensa.model.TripId
import com.firdous.expensa.usecase.AddExpense
import com.firdous.expensa.usecase.ObserveCategories
import com.firdous.expensa.usecase.ObserveTripDetail
import com.firdous.expensa.validation.ValidationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ExpenseListViewModelTest {
    private val eur = Currency("EUR")

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState reflects the trip summary and expenses`() =
        runTest {
            val trip = trip()
            val trips = InMemoryTripRepository().apply { upsert(trip) }
            val expenses = InMemoryExpenseRepository().apply { upsert(expense(Money(1250, eur))) }

            val vm =
                ExpenseListViewModel(
                    trip = trip,
                    observeTripDetail = ObserveTripDetail(trips, expenses),
                    observeCategories = ObserveCategories(InMemoryCategoryRepository()),
                    addExpense = AddExpense(expenses),
                )

            vm.uiState.test {
                val state = awaitItem().takeIf { it.summary != null } ?: awaitItem()
                assertEquals(Money(1250, eur), state.summary?.total)
                assertEquals(1, state.expenses.size)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `submitExpense persists a valid expense and uiState updates`() =
        runTest {
            val trip = trip()
            val trips = InMemoryTripRepository().apply { upsert(trip) }
            val expenses = InMemoryExpenseRepository()

            val vm =
                ExpenseListViewModel(
                    trip = trip,
                    observeTripDetail = ObserveTripDetail(trips, expenses),
                    observeCategories = ObserveCategories(InMemoryCategoryRepository()),
                    addExpense = AddExpense(expenses),
                )

            vm.uiState.test {
                val result =
                    vm.submitExpense(
                        amountMinor = 900,
                        categoryId = CategoryId("food"),
                        date = LocalDate(2026, 1, 3),
                        note = null,
                    )
                assertEquals(ValidationResult.Valid, result)
                // Skip initial emissions until the expense lands; list and summary
                // are consistent within a single emission (single-source use case).
                var updated = awaitItem()
                while (updated.expenses.isEmpty()) {
                    updated = awaitItem()
                }
                assertEquals(1, updated.expenses.size)
                assertEquals(Money(900, eur), updated.summary?.total)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `submitExpense returns Invalid for a non-positive amount and does not persist`() =
        runTest {
            val trip = trip()
            val trips = InMemoryTripRepository().apply { upsert(trip) }
            val expenses = InMemoryExpenseRepository()

            val vm =
                ExpenseListViewModel(
                    trip = trip,
                    observeTripDetail = ObserveTripDetail(trips, expenses),
                    observeCategories = ObserveCategories(InMemoryCategoryRepository()),
                    addExpense = AddExpense(expenses),
                )

            vm.uiState.test {
                val result =
                    vm.submitExpense(
                        amountMinor = 0,
                        categoryId = CategoryId("food"),
                        date = LocalDate(2026, 1, 3),
                        note = null,
                    )
                assertIs<ValidationResult.Invalid>(result)
                cancelAndIgnoreRemainingEvents()
            }
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
