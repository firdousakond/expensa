package com.firdous.expensa.di

import com.firdous.expensa.data.InMemoryCategoryRepository
import com.firdous.expensa.data.InMemoryExpenseRepository
import com.firdous.expensa.data.InMemoryTripRepository
import com.firdous.expensa.model.Category
import com.firdous.expensa.model.CategoryId
import com.firdous.expensa.model.Currency
import com.firdous.expensa.model.Money
import com.firdous.expensa.model.Trip
import com.firdous.expensa.model.TripId
import com.firdous.expensa.presentation.ExpenseListViewModel
import com.firdous.expensa.usecase.AddExpense
import com.firdous.expensa.usecase.ObserveCategories
import com.firdous.expensa.usecase.ObserveTripDetail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

/**
 * Manual composition root (Phase-1 DI). Builds the in-memory data layer, wires the
 * use cases, and exposes the ViewModel. Replaced by Koin modules once SQLDelight or
 * multiple screens arrive. See memory: di-approach.
 */
class AppContainer {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val trips = InMemoryTripRepository()
    private val expenses = InMemoryExpenseRepository()
    private val categories = InMemoryCategoryRepository()

    private val eur = Currency("EUR")

    /** A seeded demo trip so the screen has something to show. */
    val sampleTrip =
        Trip(
            id = TripId("berlin-2026"),
            name = "Berlin 2026",
            startDate = LocalDate(2026, 1, 1),
            endDate = LocalDate(2026, 1, 7),
            currency = eur,
            budget = Money(50_000, eur),
        )

    private val observeTripDetail = ObserveTripDetail(trips, expenses)
    private val observeCategories = ObserveCategories(categories)
    private val addExpense = AddExpense(expenses)

    val expenseListViewModel: ExpenseListViewModel by lazy {
        ExpenseListViewModel(sampleTrip, observeTripDetail, observeCategories, addExpense)
    }

    init {
        appScope.launch {
            trips.upsert(sampleTrip)
            categories.upsert(Category(CategoryId("food"), "Food", isBuiltIn = true))
            categories.upsert(Category(CategoryId("transport"), "Transport", isBuiltIn = true))
            categories.upsert(Category(CategoryId("lodging"), "Lodging", isBuiltIn = true))
        }
    }
}
