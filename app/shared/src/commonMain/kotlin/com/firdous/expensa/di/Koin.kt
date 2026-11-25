package com.firdous.expensa.di

import com.firdous.expensa.data.SqlDelightCategoryRepository
import com.firdous.expensa.data.SqlDelightExpenseRepository
import com.firdous.expensa.data.SqlDelightTripRepository
import com.firdous.expensa.db.ExpensaDatabase
import com.firdous.expensa.model.Currency
import com.firdous.expensa.model.Money
import com.firdous.expensa.model.Trip
import com.firdous.expensa.model.TripId
import com.firdous.expensa.presentation.ExpenseListViewModel
import com.firdous.expensa.repository.CategoryRepository
import com.firdous.expensa.repository.ExpenseRepository
import com.firdous.expensa.repository.TripRepository
import com.firdous.expensa.usecase.AddExpense
import com.firdous.expensa.usecase.ObserveCategories
import com.firdous.expensa.usecase.ObserveTripDetail
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.LocalDate
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** Provides the platform-specific SqlDriver (Android needs a Context). */
expect fun platformModule(): Module

val appModule =
    module {
        single<CoroutineDispatcher> { Dispatchers.Default }
        single { ExpensaDatabase(get()) }
        single<TripRepository> { SqlDelightTripRepository(get(), get()) }
        single<ExpenseRepository> { SqlDelightExpenseRepository(get(), get()) }
        single<CategoryRepository> { SqlDelightCategoryRepository(get(), get()) }
        single { demoTrip() }
        single { DatabaseSeeder(get(), get(), get()) }
        factory { ObserveTripDetail(get(), get()) }
        factory { ObserveCategories(get()) }
        factory { AddExpense(get()) }
        viewModelOf(::ExpenseListViewModel)
    }

fun initKoin(config: KoinApplication.() -> Unit = {}) {
    startKoin {
        config()
        modules(appModule, platformModule())
    }
}

private fun demoTrip(): Trip {
    val eur = Currency("EUR")
    return Trip(
        id = TripId("berlin-2026"),
        name = "Berlin 2026",
        startDate = LocalDate(2026, 1, 1),
        endDate = LocalDate(2026, 1, 7),
        currency = eur,
        budget = Money(50_000, eur),
    )
}
