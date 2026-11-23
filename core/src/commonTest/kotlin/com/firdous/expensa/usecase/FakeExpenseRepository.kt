package com.firdous.expensa.usecase

import com.firdous.expensa.model.Expense
import com.firdous.expensa.model.ExpenseId
import com.firdous.expensa.model.TripId
import com.firdous.expensa.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/** Test-only in-memory double for ExpenseRepository (domain tests must not touch the data layer). */
class FakeExpenseRepository : ExpenseRepository {
    private val state = MutableStateFlow<List<Expense>>(emptyList())

    override fun observeExpenses(tripId: TripId): Flow<List<Expense>> = state.map { list -> list.filter { it.tripId == tripId } }

    override suspend fun getExpense(id: ExpenseId): Expense? = state.value.find { it.id == id }

    override suspend fun upsert(expense: Expense) {
        state.update { list -> list.filterNot { it.id == expense.id } + expense }
    }

    override suspend fun delete(id: ExpenseId) {
        state.update { list -> list.filterNot { it.id == id } }
    }
}
