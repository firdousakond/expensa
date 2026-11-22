package com.firdous.expensa.data

import com.firdous.expensa.model.Category
import com.firdous.expensa.model.CategoryId
import com.firdous.expensa.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InMemoryCategoryRepository : CategoryRepository {
    private val categories = MutableStateFlow<List<Category>>(emptyList())

    override fun observeCategories(): Flow<List<Category>> = categories.asStateFlow()

    override suspend fun getCategory(id: CategoryId): Category? = categories.value.find { it.id == id }

    override suspend fun upsert(category: Category) {
        categories.update { list ->
            list.filterNot { it.id == category.id } + category
        }
    }

    override suspend fun delete(id: CategoryId) {
        categories.update { list -> list.filterNot { it.id == id } }
    }
}
