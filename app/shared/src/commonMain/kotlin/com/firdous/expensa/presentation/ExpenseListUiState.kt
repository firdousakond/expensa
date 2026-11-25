package com.firdous.expensa.presentation

import com.firdous.expensa.model.Category
import com.firdous.expensa.model.Expense
import com.firdous.expensa.model.Trip
import com.firdous.expensa.model.TripSummary

data class ExpenseListUiState(
    val trip: Trip? = null,
    val expenses: List<Expense> = emptyList(),
    val summary: TripSummary? = null,
    val categories: List<Category> = emptyList(),
)
