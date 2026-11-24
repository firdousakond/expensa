package com.firdous.expensa.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.firdous.expensa.db.ExpensaDatabase
import com.firdous.expensa.model.Currency
import com.firdous.expensa.model.Money
import com.firdous.expensa.model.Trip
import com.firdous.expensa.model.TripId
import com.firdous.expensa.repository.TripRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import com.firdous.expensa.db.Trip as TripRow

class SqlDelightTripRepository(
    private val db: ExpensaDatabase,
    private val dispatcher: CoroutineDispatcher,
) : TripRepository {
    override fun observeTrips(): Flow<List<Trip>> =
        db.tripQueries
            .selectAll()
            .asFlow()
            .mapToList(dispatcher)
            .map { rows -> rows.map { it.toDomain() } }

    override suspend fun getTrip(id: TripId): Trip? =
        withContext(dispatcher) {
            db.tripQueries
                .selectById(id.value)
                .executeAsOneOrNull()
                ?.toDomain()
        }

    override suspend fun upsert(trip: Trip): Unit =
        withContext(dispatcher) {
            db.tripQueries.upsert(
                id = trip.id.value,
                name = trip.name,
                startDate = trip.startDate.toString(),
                endDate = trip.endDate.toString(),
                currency = trip.currency.value,
                budgetMinor = trip.budget.amountMinor,
            )
        }

    override suspend fun delete(id: TripId): Unit =
        withContext(dispatcher) {
            db.tripQueries.deleteById(id.value)
        }
}

private fun TripRow.toDomain(): Trip =
    Trip(
        id = TripId(id),
        name = name,
        startDate = LocalDate.parse(startDate),
        endDate = LocalDate.parse(endDate),
        currency = Currency(currency),
        budget = Money(budgetMinor, Currency(currency)),
    )
