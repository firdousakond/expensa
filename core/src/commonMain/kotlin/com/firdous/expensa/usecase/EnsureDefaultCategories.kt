package com.firdous.expensa.usecase

import com.firdous.expensa.model.Category
import com.firdous.expensa.model.CategoryId
import com.firdous.expensa.repository.CategoryRepository
import kotlinx.coroutines.flow.first

/** Seeds a set of default categories, but only when none exist yet. */
class EnsureDefaultCategories(
    private val categories: CategoryRepository,
) {
    suspend operator fun invoke() {
        if (categories.observeCategories().first().isNotEmpty()) return
        DEFAULTS.forEach { categories.upsert(it) }
    }

    companion object {
        val DEFAULTS =
            listOf(
                Category(CategoryId("food"), "Food", isBuiltIn = true),
                Category(CategoryId("transport"), "Transport", isBuiltIn = true),
                Category(CategoryId("lodging"), "Lodging", isBuiltIn = true),
            )
    }
}
