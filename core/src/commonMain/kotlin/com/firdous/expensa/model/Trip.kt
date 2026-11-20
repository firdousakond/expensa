package com.firdous.expensa.model

import kotlinx.datetime.LocalDate

data class Trip(
    val id: TripId,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val currency: Currency,
    val budget: Money,
)
