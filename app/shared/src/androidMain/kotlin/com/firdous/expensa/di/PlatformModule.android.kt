package com.firdous.expensa.di

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import com.firdous.expensa.data.DriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module =
    module {
        single<SqlDriver> { DriverFactory(androidContext()).createDriver() }
    }

/** Android entry point: starts Koin with the application Context. */
fun initKoinAndroid(context: Context) = initKoin { androidContext(context) }
