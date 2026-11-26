package com.firdous.expensa.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.firdous.expensa.db.ExpensaDatabase
import com.firdous.expensa.model.CategoryId
import com.firdous.expensa.model.Currency
import com.firdous.expensa.model.Expense
import com.firdous.expensa.model.ExpenseId
import com.firdous.expensa.model.Money
import com.firdous.expensa.model.TripId
import com.firdous.expensa.repository.ExpenseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import com.firdous.expensa.db.Expense as ExpenseRow

class SqlDelightExpenseRepository(
    private val db: ExpensaDatabase,
    private val dispatcher: CoroutineDispatcher,
) : ExpenseRepository {
    override fun observeExpenses(tripId: TripId): Flow<List<Expense>> =
        db.expenseQueries
            .selectByTrip(tripId.value)
            .asFlow()
            .mapToList(dispatcher)
            .map { rows -> rows.map { it.toDomain() } }

    override suspend fun getExpense(id: ExpenseId): Expense? =
        withContext(dispatcher) {
            db.expenseQueries
                .selectById(id.value)
                .executeAsOneOrNull()
                ?.toDomain()
        }

    override suspend fun upsert(expense: Expense): Unit =
        withContext(dispatcher) {
            db.expenseQueries.upsert(
                id = expense.id.value,
                tripId = expense.tripId.value,
                amountMinor = expense.amount.amountMinor,
                currency = expense.amount.currency.value,
                categoryId = expense.categoryId.value,
                date = expense.date.toString(),
                note = expense.note,
            )
        }

    override suspend fun delete(id: ExpenseId): Unit =
        withContext(dispatcher) {
            db.expenseQueries.deleteById(id.value)
        }
}

private fun ExpenseRow.toDomain(): Expense =
    Expense(
        id = ExpenseId(id),
        tripId = TripId(tripId),
        amount = Money(amountMinor, Currency(currency)),
        categoryId = CategoryId(categoryId),
        date = LocalDate.parse(date),
        note = note,
    )
