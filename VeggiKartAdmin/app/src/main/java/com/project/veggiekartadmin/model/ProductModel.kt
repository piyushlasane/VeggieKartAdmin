package com.project.veggiekartadmin.model

data class ProductModel(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val price: String = "",
    val actualPrice: String = "",
    val images: List<String> = emptyList(),
)