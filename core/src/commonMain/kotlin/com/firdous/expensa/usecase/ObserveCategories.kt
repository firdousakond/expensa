package com.firdous.expensa.usecase

import com.firdous.expensa.model.Category
import com.firdous.expensa.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow

/** Observes the available expense categories (for pickers and AI validation). */
class ObserveCategories(
    private val categories: CategoryRepository,
) {
    operator fun invoke(): Flow<List<Category>> = categories.observeCategories()
}
