package com.project.veggiekartadmin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.project.veggiekartadmin.model.CategoryModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class CategoryViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val categoriesCollection = firestore
        .collection("data")
        .document("stock")
        .collection("categories")

    private val _categories = MutableStateFlow<List<CategoryModel>>(emptyList())
    val categories: StateFlow<List<CategoryModel>> = _categories.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadCategories() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val snapshot = categoriesCollection.get().await()
                val categoryList = snapshot.documents.mapNotNull {
                    it.toObject(CategoryModel::class.java)
                }
                _categories.value = categoryList
            } catch (e: Exception) {
                _categories.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveCategory(
        categoryId: String?,
        name: String,
        imageUrl: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val id = categoryId ?: UUID.randomUUID().toString()
                val category = CategoryModel(
                    id = id,
                    name = name,
                    imageUrl = imageUrl
                )
                categoriesCollection.document(id).set(category).await()
                loadCategories()
                onResult(true, if (categoryId == null) "Category added" else "Category updated")
            } catch (e: Exception) {
                onResult(false, e.localizedMessage ?: "Failed to save category")
            }
        }
    }

    fun deleteCategory(categoryId: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                categoriesCollection.document(categoryId).delete().await()
                loadCategories()
                onResult(true, "Category deleted")
            } catch (e: Exception) {
                onResult(false, e.localizedMessage ?: "Failed to delete category")
            }
        }
    }

    fun getCategoryById(categoryId: String, onResult: (CategoryModel?) -> Unit) {
        viewModelScope.launch {
            try {
                val doc = categoriesCollection.document(categoryId).get().await()
                onResult(doc.toObject(CategoryModel::class.java))
            } catch (e: Exception) {
                onResult(null)
            }
        }
    }
}