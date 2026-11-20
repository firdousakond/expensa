package com.firdous.expensa.model

data class Category(
    val id: CategoryId,
    val name: String,
    val isBuiltIn: Boolean = false,
)
