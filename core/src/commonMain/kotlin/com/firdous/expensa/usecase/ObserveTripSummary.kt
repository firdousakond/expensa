package com.firdous.expensa.usecase

import com.firdous.expensa.model.TripId
import com.firdous.expensa.model.TripSummary
import com.firdous.expensa.repository.ExpenseRepository
import com.firdous.expensa.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

/**
 * Reactive summary for a trip: re-emits whenever the trip or its expenses change.
 * Emits null when the trip no longer exists.
 */
class ObserveTripSummary(
    private val trips: TripRepository,
    private val expenses: ExpenseRepository,
) {
    operator fun invoke(tripId: TripId): Flow<TripSummary?> =
        combine(
            trips.observeTrips().map { list -> list.find { it.id == tripId } },
            expenses.observeExpenses(tripId),
        ) { trip, tripExpenses ->
            trip?.let {
                TripSummary(
                    total = tripTotal(it, tripExpenses),
                    perCategory = perCategoryBreakdown(it, tripExpenses),
                    remaining = budgetRemaining(it, tripExpenses),
                )
            }
        }
}
