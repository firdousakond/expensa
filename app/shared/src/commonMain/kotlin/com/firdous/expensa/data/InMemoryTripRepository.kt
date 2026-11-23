package com.firdous.expensa.data

import com.firdous.expensa.model.Trip
import com.firdous.expensa.model.TripId
import com.firdous.expensa.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InMemoryTripRepository : TripRepository {
    private val trips = MutableStateFlow<List<Trip>>(emptyList())

    override fun observeTrips(): Flow<List<Trip>> = trips.asStateFlow()

    override suspend fun getTrip(id: TripId): Trip? = trips.value.find { it.id == id }

    override suspend fun upsert(trip: Trip) {
        trips.update { list ->
            list.filterNot { it.id == trip.id } + trip
        }
    }

    override suspend fun delete(id: TripId) {
        trips.update { list -> list.filterNot { it.id == id } }
    }
}
