package com.project.veggiekartadmin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.project.veggiekartadmin.model.ProductModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ProductViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val productsCollection = firestore
        .collection("data")
        .document("stock")
        .collection("products")

    private val _products = MutableStateFlow<List<ProductModel>>(emptyList())
    val products: StateFlow<List<ProductModel>> = _products.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadProducts() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val snapshot = productsCollection.get().await()
                val productList = snapshot.documents.mapNotNull {
                    it.toObject(ProductModel::class.java)
                }
                _products.value = productList
            } catch (e: Exception) {
                _products.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveProduct(
        productId: String?,
        title: String,
        description: String,
        category: String,
        price: String,
        actualPrice: String,
        images: List<String>,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val id = productId ?: UUID.randomUUID().toString()
                val product = ProductModel(
                    id = id,
                    title = title,
                    description = description,
                    category = category,
                    price = price,
                    actualPrice = actualPrice,
                    images = images
                )
                productsCollection.document(id).set(product).await()
                loadProducts()
                onResult(true, if (productId == null) "Product added" else "Product updated")
            } catch (e: Exception) {
                onResult(false, e.localizedMessage ?: "Failed to save product")
            }
        }
    }

    fun deleteProduct(productId: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                productsCollection.document(productId).delete().await()
                loadProducts()
                onResult(true, "Product deleted")
            } catch (e: Exception) {
                onResult(false, e.localizedMessage ?: "Failed to delete product")
            }
        }
    }

    fun getProductById(productId: String, onResult: (ProductModel?) -> Unit) {
        viewModelScope.launch {
            try {
                val doc = productsCollection.document(productId).get().await()
                onResult(doc.toObject(ProductModel::class.java))
            } catch (e: Exception) {
                onResult(null)
            }
        }
    }
}