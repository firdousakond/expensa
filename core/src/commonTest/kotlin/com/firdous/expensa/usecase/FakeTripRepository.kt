package com.firdous.expensa.usecase

import com.firdous.expensa.model.Trip
import com.firdous.expensa.model.TripId
import com.firdous.expensa.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Test-only in-memory double for TripRepository (domain tests must not touch the data layer). */
class FakeTripRepository : TripRepository {
    private val state = MutableStateFlow<List<Trip>>(emptyList())

    override fun observeTrips(): Flow<List<Trip>> = state.asStateFlow()

    override suspend fun getTrip(id: TripId): Trip? = state.value.find { it.id == id }

    override suspend fun upsert(trip: Trip) {
        state.update { list -> list.filterNot { it.id == trip.id } + trip }
    }

    override suspend fun delete(id: TripId) {
        state.update { list -> list.filterNot { it.id == id } }
    }
}
