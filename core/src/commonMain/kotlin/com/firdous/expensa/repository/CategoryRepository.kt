package com.firdous.expensa.repository

import com.firdous.expensa.model.Category
import com.firdous.expensa.model.CategoryId
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun observeCategories(): Flow<List<Category>>

    suspend fun getCategory(id: CategoryId): Category?

    suspend fun upsert(category: Category)

    suspend fun delete(id: CategoryId)
}
