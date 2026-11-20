package com.firdous.expensa.model

import kotlin.jvm.JvmInline

@JvmInline
value class CategoryId(
    val value: String,
)

@JvmInline
value class TripId(
    val value: String,
)

@JvmInline
value class ExpenseId(
    val value: String,
)

@JvmInline
value class Currency(
    val value: String,
)
