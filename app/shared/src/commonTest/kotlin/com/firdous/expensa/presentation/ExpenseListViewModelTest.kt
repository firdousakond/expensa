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
import com.firdous.expensa.usecase.AddTrip
import com.firdous.expensa.usecase.EnsureDefaultCategories
import com.firdous.expensa.usecase.ObserveCategories
import com.firdous.expensa.usecase.ObserveTripDetail
import com.firdous.expensa.validation.ValidationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
import kotlin.test.assertTrue

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
            val trips = InMemoryTripRepository().apply { upsert(trip()) }
            val expenses = InMemoryExpenseRepository().apply { upsert(expense(Money(1250, eur))) }
            val vm = viewModel(trips, expenses)

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
            val trips = InMemoryTripRepository().apply { upsert(trip()) }
            val expenses = InMemoryExpenseRepository()
            val vm = viewModel(trips, expenses)

            vm.uiState.test {
                val result = vm.submitExpense(900, CategoryId("food"), LocalDate(2026, 1, 3), null)
                assertEquals(ValidationResult.Valid, result)
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
    fun `submitExpense returns Invalid for a non-positive amount`() =
        runTest {
            val trips = InMemoryTripRepository().apply { upsert(trip()) }
            val expenses = InMemoryExpenseRepository()
            val vm = viewModel(trips, expenses)

            vm.uiState.test {
                val result = vm.submitExpense(0, CategoryId("food"), LocalDate(2026, 1, 3), null)
                assertIs<ValidationResult.Invalid>(result)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `createTrip persists the trip and seeds default categories`() =
        runTest {
            val trips = InMemoryTripRepository()
            val expenses = InMemoryExpenseRepository()
            val categories = InMemoryCategoryRepository()
            val vm = viewModel(trips, expenses, categories)

            vm.uiState.test {
                assertEquals(null, awaitItem().trip)
                val result =
                    vm.createTrip("Berlin", LocalDate(2026, 1, 1), LocalDate(2026, 1, 7), "EUR", 50_000)
                assertEquals(ValidationResult.Valid, result)
                var state = awaitItem()
                while (state.trip == null) {
                    state = awaitItem()
                }
                assertEquals("Berlin", state.trip.name)
                cancelAndIgnoreRemainingEvents()
            }
            assertTrue(categories.observeCategories().first().isNotEmpty())
        }

    @Test
    fun `createTrip returns Invalid when end date is before start`() =
        runTest {
            val vm = viewModel(InMemoryTripRepository(), InMemoryExpenseRepository())

            val result =
                vm.createTrip("Bad", LocalDate(2026, 1, 7), LocalDate(2026, 1, 1), "EUR", 50_000)

            assertIs<ValidationResult.Invalid>(result)
        }

    private fun viewModel(
        trips: InMemoryTripRepository,
        expenses: InMemoryExpenseRepository,
        categories: InMemoryCategoryRepository = InMemoryCategoryRepository(),
    ) = ExpenseListViewModel(
        observeTripDetail = ObserveTripDetail(trips, expenses),
        observeCategories = ObserveCategories(categories),
        addExpense = AddExpense(expenses),
        addTrip = AddTrip(trips),
        ensureDefaultCategories = EnsureDefaultCategories(categories),
        trips = trips,
    )

    private fun trip() =
        Trip(
            id = TripId("berlin"),
            name = "Test Trip",
            startDate = LocalDate(2026, 1, 1),
            endDate = LocalDate(2026, 1, 7),
            currency = eur,
            budget = Money(50_000, eur),
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
