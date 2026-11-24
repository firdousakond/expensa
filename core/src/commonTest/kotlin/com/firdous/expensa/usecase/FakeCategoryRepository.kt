package com.firdous.expensa.usecase

import com.firdous.expensa.model.Category
import com.firdous.expensa.model.CategoryId
import com.firdous.expensa.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Test-only in-memory double for CategoryRepository. */
class FakeCategoryRepository : CategoryRepository {
    private val state = MutableStateFlow<List<Category>>(emptyList())

    override fun observeCategories(): Flow<List<Category>> = state.asStateFlow()

    override suspend fun getCategory(id: CategoryId): Category? = state.value.find { it.id == id }

    override suspend fun upsert(category: Category) {
        state.update { list -> list.filterNot { it.id == category.id } + category }
    }

    override suspend fun delete(id: CategoryId) {
        state.update { list -> list.filterNot { it.id == id } }
    }
}
