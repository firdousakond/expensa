package com.firdous.expensa.data

import app.cash.sqldelight.db.SqlDriver

/** Creates the platform-specific SQLDelight driver for ExpensaDatabase. */
expect class DriverFactory {
    fun createDriver(): SqlDriver
}
