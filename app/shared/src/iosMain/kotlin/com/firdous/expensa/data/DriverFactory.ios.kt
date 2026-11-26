package com.firdous.expensa.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.firdous.expensa.db.ExpensaDatabase

actual class DriverFactory {
    actual fun createDriver(): SqlDriver = NativeSqliteDriver(ExpensaDatabase.Schema, "expensa.db")
}
