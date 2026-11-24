package com.firdous.expensa.usecase

import app.cash.turbine.test
import com.firdous.expensa.model.Category
import com.firdous.expensa.model.CategoryId
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ObserveCategoriesTest {
    @Test
    fun `emits the current categories and updates on change`() =
        runTest {
            val repo = FakeCategoryRepository()
            repo.upsert(Category(CategoryId("food"), "Food"))
            val observeCategories = ObserveCategories(repo)

            observeCategories().test {
                assertEquals(listOf(Category(CategoryId("food"), "Food")), awaitItem())

                repo.upsert(Category(CategoryId("transport"), "Transport"))
                assertEquals(2, awaitItem().size)
                cancelAndIgnoreRemainingEvents()
            }
        }
}
