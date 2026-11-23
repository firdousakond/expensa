package com.firdous.expensa.model

/** Aggregated spend view for a trip: total, breakdown by category, and budget left. */
data class TripSummary(
    val total: Money,
    val perCategory: Map<CategoryId, Money>,
    val remaining: Money,
)
