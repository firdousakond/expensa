package com.firdous.expensa.model

import kotlinx.datetime.LocalDate

data class Expense(
    val id: ExpenseId,
    val tripId: TripId,
    val amount: Money,
    val categoryId: CategoryId,
    val date: LocalDate,
    val note: String? = null,
)
