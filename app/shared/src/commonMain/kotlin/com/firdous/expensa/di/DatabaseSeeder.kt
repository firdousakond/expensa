package com.firdous.expensa.di

import com.firdous.expensa.model.Category
import com.firdous.expensa.model.CategoryId
import com.firdous.expensa.model.Trip
import com.firdous.expensa.repository.CategoryRepository
import com.firdous.expensa.repository.TripRepository

/** Seeds the demo trip and default categories on first launch (idempotent). */
class DatabaseSeeder(
    private val trips: TripRepository,
    private val categories: CategoryRepository,
    private val sampleTrip: Trip,
) {
    suspend fun seed() {
        if (trips.getTrip(sampleTrip.id) != null) return
        trips.upsert(sampleTrip)
        categories.upsert(Category(CategoryId("food"), "Food", isBuiltIn = true))
        categories.upsert(Category(CategoryId("transport"), "Transport", isBuiltIn = true))
        categories.upsert(Category(CategoryId("lodging"), "Lodging", isBuiltIn = true))
    }
}
