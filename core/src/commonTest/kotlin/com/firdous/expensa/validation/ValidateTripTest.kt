package com.firdous.expensa.validation

import com.firdous.expensa.model.Currency
import com.firdous.expensa.model.Money
import com.firdous.expensa.model.Trip
import com.firdous.expensa.model.TripId
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ValidateTripTest {
    private val eur = Currency("EUR")

    @Test
    fun `a well-formed trip is valid`() {
        val trip = trip(start = LocalDate(2026, 1, 1), end = LocalDate(2026, 1, 7))

        assertEquals(ValidationResult.Valid, validateTrip(trip))
    }

    @Test
    fun `a single-day trip is valid`() {
        val sameDay = LocalDate(2026, 1, 1)
        val trip = trip(start = sameDay, end = sameDay)

        assertEquals(ValidationResult.Valid, validateTrip(trip))
    }

    @Test
    fun `reports EndDateBeforeStart when end date is before start date`() {
        val trip = trip(start = LocalDate(2026, 1, 7), end = LocalDate(2026, 1, 1))

        val result = assertIs<ValidationResult.Invalid>(validateTrip(trip))
        assertTrue(ValidationError.EndDateBeforeStart in result.errors)
    }

    private fun trip(
        start: LocalDate,
        end: LocalDate,
    ) = Trip(
        id = TripId("berlin"),
        name = "Test Trip",
        startDate = start,
        endDate = end,
        currency = eur,
        budget = Money(50000, eur),
    )
}
