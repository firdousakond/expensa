package com.firdous.expensa.usecase

import com.firdous.expensa.model.Category
import com.firdous.expensa.model.CategoryId
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EnsureDefaultCategoriesTest {
    @Test
    fun `seeds defaults when there are no categories`() =
        runTest {
            val repo = FakeCategoryRepository()
            val ensure = EnsureDefaultCategories(repo)

            ensure()

            assertTrue(repo.observeCategories().first().isNotEmpty())
        }

    @Test
    fun `does nothing when categories already exist`() =
        runTest {
            val repo = FakeCategoryRepository()
            repo.upsert(Category(CategoryId("custom"), "Custom"))
            val ensure = EnsureDefaultCategories(repo)

            ensure()

            assertEquals(listOf(Category(CategoryId("custom"), "Custom")), repo.observeCategories().first())
        }
}
