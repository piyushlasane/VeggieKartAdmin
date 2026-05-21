package com.project.veggiekartadmin.navigation

object Routes {
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val ADD_EDIT_PRODUCT = "add-edit-product/{productId}"
    const val ADD_EDIT_CATEGORY = "add-edit-category/{categoryId}"
    const val BANNERS = "banners"

    fun addEditProduct(productId: String = "null") = "add-edit-product/$productId"
    fun addEditCategory(categoryId: String = "null") = "add-edit-category/$categoryId"
}