package com.firdous.expensa.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.firdous.expensa.db.ExpensaDatabase
import com.firdous.expensa.model.Category
import com.firdous.expensa.model.CategoryId
import com.firdous.expensa.repository.CategoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import com.firdous.expensa.db.Category as CategoryRow

class SqlDelightCategoryRepository(
    private val db: ExpensaDatabase,
    private val dispatcher: CoroutineDispatcher,
) : CategoryRepository {
    override fun observeCategories(): Flow<List<Category>> =
        db.categoryQueries
            .selectAll()
            .asFlow()
            .mapToList(dispatcher)
            .map { rows -> rows.map { it.toDomain() } }

    override suspend fun getCategory(id: CategoryId): Category? =
        withContext(dispatcher) {
            db.categoryQueries
                .selectById(id.value)
                .executeAsOneOrNull()
                ?.toDomain()
        }

    override suspend fun upsert(category: Category): Unit =
        withContext(dispatcher) {
            db.categoryQueries.upsert(
                id = category.id.value,
                name = category.name,
                isBuiltIn = if (category.isBuiltIn) 1L else 0L,
            )
        }

    override suspend fun delete(id: CategoryId): Unit =
        withContext(dispatcher) {
            db.categoryQueries.deleteById(id.value)
        }
}

private fun CategoryRow.toDomain(): Category =
    Category(
        id = CategoryId(id),
        name = name,
        isBuiltIn = isBuiltIn != 0L,
    )
