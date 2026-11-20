package com.firdous.expensa.repository

import com.firdous.expensa.model.Expense
import com.firdous.expensa.model.ExpenseId
import com.firdous.expensa.model.TripId
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun observeExpenses(tripId: TripId): Flow<List<Expense>>

    suspend fun getExpense(id: ExpenseId): Expense?

    suspend fun upsert(expense: Expense)

    suspend fun delete(id: ExpenseId)
}
