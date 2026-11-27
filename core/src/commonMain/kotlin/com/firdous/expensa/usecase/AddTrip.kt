package com.firdous.expensa.usecase

import com.firdous.expensa.model.Trip
import com.firdous.expensa.repository.TripRepository
import com.firdous.expensa.validation.ValidationResult
import com.firdous.expensa.validation.validateTrip

/** Validates a trip and persists it only if valid. */
class AddTrip(
    private val trips: TripRepository,
) {
    suspend operator fun invoke(trip: Trip): ValidationResult {
        val result = validateTrip(trip)
        if (result is ValidationResult.Valid) {
            trips.upsert(trip)
        }
        return result
    }
}
