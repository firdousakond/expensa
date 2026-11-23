package com.firdous.expensa.data

import com.firdous.expensa.model.Expense
import com.firdous.expensa.model.ExpenseId
import com.firdous.expensa.model.TripId
import com.firdous.expensa.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class InMemoryExpenseRepository : ExpenseRepository {
    private val expenses = MutableStateFlow<List<Expense>>(emptyList())

    override fun observeExpenses(tripId: TripId): Flow<List<Expense>> = expenses.map { list -> list.filter { it.tripId == tripId } }

    override suspend fun getExpense(id: ExpenseId): Expense? = expenses.value.find { it.id == id }

    override suspend fun upsert(expense: Expense) {
        expenses.update { list ->
            list.filterNot { it.id == expense.id } + expense
        }
    }

    override suspend fun delete(id: ExpenseId) {
        expenses.update { list -> list.filterNot { it.id == id } }
    }
}
