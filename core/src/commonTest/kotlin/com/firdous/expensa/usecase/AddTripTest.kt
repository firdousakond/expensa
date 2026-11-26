package com.firdous.expensa.usecase

import com.firdous.expensa.model.Currency
import com.firdous.expensa.model.Money
import com.firdous.expensa.model.Trip
import com.firdous.expensa.model.TripId
import com.firdous.expensa.validation.ValidationResult
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class AddTripTest {
    private val eur = Currency("EUR")

    @Test
    fun `a valid trip is persisted and returns Valid`() =
        runTest {
            val repo = FakeTripRepository()
            val addTrip = AddTrip(repo)
            val trip = trip(start = LocalDate(2026, 1, 1), end = LocalDate(2026, 1, 7))

            val result = addTrip(trip)

            assertEquals(ValidationResult.Valid, result)
            assertEquals(trip, repo.getTrip(trip.id))
        }

    @Test
    fun `an invalid trip is not persisted and returns Invalid`() =
        runTest {
            val repo = FakeTripRepository()
            val addTrip = AddTrip(repo)
            val trip = trip(start = LocalDate(2026, 1, 7), end = LocalDate(2026, 1, 1))

            val result = addTrip(trip)

            assertIs<ValidationResult.Invalid>(result)
            assertNull(repo.getTrip(trip.id))
        }

    private fun trip(
        start: LocalDate,
        end: LocalDate,
    ) = Trip(
        id = TripId("berlin"),
        name = "Berlin",
        startDate = start,
        endDate = end,
        currency = eur,
        budget = Money(50_000, eur),
    )
}
