package com.firdous.expensa.data

import app.cash.turbine.test
import com.firdous.expensa.model.CategoryId
import com.firdous.expensa.model.Currency
import com.firdous.expensa.model.Expense
import com.firdous.expensa.model.ExpenseId
import com.firdous.expensa.model.Money
import com.firdous.expensa.model.TripId
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class InMemoryExpenseRepositoryTest {
    private val eur = Currency("EUR")
    private val berlin = TripId("berlin")

    @Test
    fun `upsert then observe emits the expense`() =
        runTest {
            val repo = InMemoryExpenseRepository()
            val expense = expense(ExpenseId("e1"), berlin, Money(1250, eur))

            repo.upsert(expense)

            repo.observeExpenses(berlin).test {
                assertEquals(listOf(expense), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `upsert with the same id replaces instead of duplicating`() =
        runTest {
            val repo = InMemoryExpenseRepository()
            repo.upsert(expense(ExpenseId("e1"), berlin, Money(1250, eur)))
            repo.upsert(expense(ExpenseId("e1"), berlin, Money(999, eur)))

            repo.observeExpenses(berlin).test {
                val list = awaitItem()
                assertEquals(1, list.size)
                assertEquals(Money(999, eur), list.single().amount)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `delete removes the expense`() =
        runTest {
            val repo = InMemoryExpenseRepository()
            val expense = expense(ExpenseId("e1"), berlin, Money(1250, eur))
            repo.upsert(expense)
            repo.delete(expense.id)

            repo.observeExpenses(berlin).test {
                assertEquals(emptyList(), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `observeExpenses returns only the given trip's expenses`() =
        runTest {
            val repo = InMemoryExpenseRepository()
            val berlinExpense = expense(ExpenseId("e1"), berlin, Money(1250, eur))
            val parisExpense = expense(ExpenseId("e2"), TripId("paris"), Money(900, eur))
            repo.upsert(berlinExpense)
            repo.upsert(parisExpense)

            repo.observeExpenses(berlin).test {
                assertEquals(listOf(berlinExpense), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    private fun expense(
        id: ExpenseId,
        tripId: TripId,
        amount: Money,
    ) = Expense(
        id = id,
        tripId = tripId,
        amount = amount,
        categoryId = CategoryId("food"),
        date = LocalDate(2026, 1, 1),
        note = null,
    )
}
