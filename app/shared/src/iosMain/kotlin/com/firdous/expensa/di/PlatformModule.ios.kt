package com.firdous.expensa.di

import app.cash.sqldelight.db.SqlDriver
import com.firdous.expensa.data.DriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module =
    module {
        single<SqlDriver> { DriverFactory().createDriver() }
    }
