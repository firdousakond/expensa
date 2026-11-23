package com.firdous.expensa.usecase

import com.firdous.expensa.model.TripDetail
import com.firdous.expensa.model.TripId
import com.firdous.expensa.model.TripSummary
import com.firdous.expensa.repository.ExpenseRepository
import com.firdous.expensa.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

/**
 * Reactive detail for a trip — expenses plus the derived summary — from a single
 * combine, so the list and the summary are always consistent in one emission.
 * Emits null when the trip no longer exists.
 */
class ObserveTripDetail(
    private val trips: TripRepository,
    private val expenses: ExpenseRepository,
) {
    operator fun invoke(tripId: TripId): Flow<TripDetail?> =
        combine(
            trips.observeTrips().map { list -> list.find { it.id == tripId } },
            expenses.observeExpenses(tripId),
        ) { trip, tripExpenses ->
            trip?.let {
                TripDetail(
                    expenses = tripExpenses,
                    summary =
                        TripSummary(
                            total = tripTotal(it, tripExpenses),
                            perCategory = perCategoryBreakdown(it, tripExpenses),
                            remaining = budgetRemaining(it, tripExpenses),
                        ),
                )
            }
        }
}
