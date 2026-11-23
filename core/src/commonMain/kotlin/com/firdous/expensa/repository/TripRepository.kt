package com.firdous.expensa.repository

import com.firdous.expensa.model.Trip
import com.firdous.expensa.model.TripId
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    fun observeTrips(): Flow<List<Trip>>

    suspend fun getTrip(id: TripId): Trip?

    suspend fun upsert(trip: Trip)

    suspend fun delete(id: TripId)
}
