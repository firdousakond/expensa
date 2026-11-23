package com.firdous.expensa.model

/** A trip's expenses together with its derived summary, as one consistent snapshot. */
data class TripDetail(
    val expenses: List<Expense>,
    val summary: TripSummary,
)
