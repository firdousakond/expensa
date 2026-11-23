package com.firdous.expensa.data

import app.cash.turbine.test
import com.firdous.expensa.model.Category
import com.firdous.expensa.model.CategoryId
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class InMemoryCategoryRepositoryTest {
    @Test
    fun `upsert then observe emits the category`() =
        runTest {
            val repo = InMemoryCategoryRepository()
            val category = Category(CategoryId("food"), "Food")

            repo.upsert(category)

            repo.observeCategories().test {
                assertEquals(listOf(category), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `upsert with the same id replaces instead of duplicating`() =
        runTest {
            val repo = InMemoryCategoryRepository()
            repo.upsert(Category(CategoryId("food"), "Food"))
            repo.upsert(Category(CategoryId("food"), "Groceries"))

            repo.observeCategories().test {
                val list = awaitItem()
                assertEquals(1, list.size)
                assertEquals("Groceries", list.single().name)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `delete removes the category`() =
        runTest {
            val repo = InMemoryCategoryRepository()
            val category = Category(CategoryId("food"), "Food")
            repo.upsert(category)
            repo.delete(category.id)

            repo.observeCategories().test {
                assertEquals(emptyList(), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `getCategory returns the category or null`() =
        runTest {
            val repo = InMemoryCategoryRepository()
            val category = Category(CategoryId("food"), "Food")
            repo.upsert(category)

            assertEquals(category, repo.getCategory(CategoryId("food")))
            assertNull(repo.getCategory(CategoryId("transport")))
        }
}
