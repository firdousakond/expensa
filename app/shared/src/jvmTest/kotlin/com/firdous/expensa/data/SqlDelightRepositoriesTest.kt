package com.firdous.expensa.data

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.cash.turbine.test
import com.firdous.expensa.db.ExpensaDatabase
import com.firdous.expensa.model.Category
import com.firdous.expensa.model.CategoryId
import com.firdous.expensa.model.Currency
import com.firdous.expensa.model.Expense
import com.firdous.expensa.model.ExpenseId
import com.firdous.expensa.model.Money
import com.firdous.expensa.model.Trip
import com.firdous.expensa.model.TripId
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class SqlDelightRepositoriesTest {
    private val eur = Currency("EUR")

    private fun db(): ExpensaDatabase {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        ExpensaDatabase.Schema.create(driver)
        return ExpensaDatabase(driver)
    }

    @Test
    fun `trip round-trips through the database`() =
        runTest {
            val repo = SqlDelightTripRepository(db(), UnconfinedTestDispatcher(testScheduler))
            val trip = trip()

            repo.upsert(trip)

            assertEquals(trip, repo.getTrip(trip.id))
            repo.observeTrips().test {
                assertEquals(listOf(trip), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `trip delete removes it`() =
        runTest {
            val repo = SqlDelightTripRepository(db(), UnconfinedTestDispatcher(testScheduler))
            val trip = trip()
            repo.upsert(trip)

            repo.delete(trip.id)

            assertNull(repo.getTrip(trip.id))
        }

    @Test
    fun `expenses are scoped to their trip and round-trip`() =
        runTest {
            val repo = SqlDelightExpenseRepository(db(), UnconfinedTestDispatcher(testScheduler))
            val berlin = expense("e1", TripId("berlin"), Money(1250, eur))
            val paris = expense("e2", TripId("paris"), Money(900, eur))
            repo.upsert(berlin)
            repo.upsert(paris)

            assertEquals(berlin, repo.getExpense(ExpenseId("e1")))
            repo.observeExpenses(TripId("berlin")).test {
                assertEquals(listOf(berlin), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `expense upsert replaces by id`() =
        runTest {
            val repo = SqlDelightExpenseRepository(db(), UnconfinedTestDispatcher(testScheduler))
            repo.upsert(expense("e1", TripId("berlin"), Money(1250, eur)))
            repo.upsert(expense("e1", TripId("berlin"), Money(999, eur)))

            repo.observeExpenses(TripId("berlin")).test {
                val list = awaitItem()
                assertEquals(1, list.size)
                assertEquals(Money(999, eur), list.single().amount)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `category round-trips with isBuiltIn flag`() =
        runTest {
            val repo = SqlDelightCategoryRepository(db(), UnconfinedTestDispatcher(testScheduler))
            val category = Category(CategoryId("food"), "Food", isBuiltIn = true)

            repo.upsert(category)

            assertEquals(category, repo.getCategory(CategoryId("food")))
        }

    @Test
    fun `categories are observed and deletable`() =
        runTest {
            val repo = SqlDelightCategoryRepository(db(), UnconfinedTestDispatcher(testScheduler))
            val food = Category(CategoryId("food"), "Food", isBuiltIn = true)
            val transport = Category(CategoryId("transport"), "Transport")
            repo.upsert(food)
            repo.upsert(transport)

            repo.observeCategories().test {
                assertEquals(listOf(food, transport), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }

            repo.delete(CategoryId("food"))
            assertNull(repo.getCategory(CategoryId("food")))
        }

    private fun trip() =
        Trip(
            id = TripId("berlin"),
            name = "Berlin",
            startDate = LocalDate(2026, 1, 1),
            endDate = LocalDate(2026, 1, 7),
            currency = eur,
            budget = Money(50_000, eur),
        )

    private fun expense(
        id: String,
        tripId: TripId,
        amount: Money,
    ) = Expense(
        id = ExpenseId(id),
        tripId = tripId,
        amount = amount,
        categoryId = CategoryId("food"),
        date = LocalDate(2026, 1, 3),
        note = null,
    )
}
