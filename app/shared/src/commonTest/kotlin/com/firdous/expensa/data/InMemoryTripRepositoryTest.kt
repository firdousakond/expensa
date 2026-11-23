package com.firdous.expensa.data

import app.cash.turbine.test
import com.firdous.expensa.model.Currency
import com.firdous.expensa.model.Money
import com.firdous.expensa.model.Trip
import com.firdous.expensa.model.TripId
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class InMemoryTripRepositoryTest {
    private val eur = Currency("EUR")

    @Test
    fun `upsert then observe emits the trip`() =
        runTest {
            val repo = InMemoryTripRepository()
            val trip = trip(TripId("berlin"))

            repo.upsert(trip)

            repo.observeTrips().test {
                assertEquals(listOf(trip), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `upsert with the same id replaces instead of duplicating`() =
        runTest {
            val repo = InMemoryTripRepository()
            repo.upsert(trip(TripId("berlin"), name = "First"))
            repo.upsert(trip(TripId("berlin"), name = "Renamed"))

            repo.observeTrips().test {
                val list = awaitItem()
                assertEquals(1, list.size)
                assertEquals("Renamed", list.single().name)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `delete removes the trip`() =
        runTest {
            val repo = InMemoryTripRepository()
            val trip = trip(TripId("berlin"))
            repo.upsert(trip)
            repo.delete(trip.id)

            repo.observeTrips().test {
                assertEquals(emptyList(), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `getTrip returns the trip or null`() =
        runTest {
            val repo = InMemoryTripRepository()
            val trip = trip(TripId("berlin"))
            repo.upsert(trip)

            assertEquals(trip, repo.getTrip(TripId("berlin")))
            assertNull(repo.getTrip(TripId("paris")))
        }

    private fun trip(
        id: TripId,
        name: String = "Test Trip",
    ) = Trip(
        id = id,
        name = name,
        startDate = LocalDate(2026, 1, 1),
        endDate = LocalDate(2026, 1, 7),
        currency = eur,
        budget = Money(50000, eur),
    )
}
