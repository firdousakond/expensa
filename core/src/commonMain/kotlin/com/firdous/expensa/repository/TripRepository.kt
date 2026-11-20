package com.firdous.expensa.repository

import com.firdous.expensa.model.Category
import com.firdous.expensa.model.CategoryId
import com.firdous.expensa.model.Trip
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    fun observeTrip(): Flow<List<Trip>>

    suspend fun getCategory(id: CategoryId): Category?

    suspend fun upsert(category: Category)

    suspend fun delete(id: CategoryId)
}
