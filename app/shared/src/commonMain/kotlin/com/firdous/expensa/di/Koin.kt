package com.firdous.expensa.di

import com.firdous.expensa.data.SqlDelightCategoryRepository
import com.firdous.expensa.data.SqlDelightExpenseRepository
import com.firdous.expensa.data.SqlDelightTripRepository
import com.firdous.expensa.db.ExpensaDatabase
import com.firdous.expensa.presentation.ExpenseListViewModel
import com.firdous.expensa.repository.CategoryRepository
import com.firdous.expensa.repository.ExpenseRepository
import com.firdous.expensa.repository.TripRepository
import com.firdous.expensa.usecase.AddExpense
import com.firdous.expensa.usecase.ObserveCategories
import com.firdous.expensa.usecase.ObserveTripDetail
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
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
