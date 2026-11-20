package com.firdous.expensa.usecase

import com.firdous.expensa.model.CategoryId
import com.firdous.expensa.model.Expense
import com.firdous.expensa.model.Money
import com.firdous.expensa.model.Trip

fun tripTotal(
    trip: Trip,
    expenses: List<Expense>,
): Money =
    expenses
        .filter { it.tripId == trip.id }
        .map { it.amount }
        .fold(Money.zero(trip.currency)) { acc, money -> acc + money }

fun perCategoryBreakdown(
    trip: Trip,
    expenses: List<Expense>,
): Map<CategoryId, Money> =
    expenses
        .filter { it.tripId == trip.id }
        .groupBy { it.categoryId }
        .mapValues { (_, categoryExpenses) ->
            categoryExpenses
                .map { it.amount }
                .fold(Money.zero(trip.currency)) { acc, money -> acc + money }
        }

fun budgetRemaining(
    trip: Trip,
    expenses: List<Expense>,
): Money = trip.budget - tripTotal(trip, expenses)
