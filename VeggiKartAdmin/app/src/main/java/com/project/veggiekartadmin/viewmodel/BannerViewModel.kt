package com.project.veggiekartadmin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class BannerViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val bannersDoc = firestore
        .collection("data")
        .document("banners")

    private val _banners = MutableStateFlow<List<String>>(emptyList())
    val banners: StateFlow<List<String>> = _banners.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    fun loadBanners() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val doc = bannersDoc.get().await()
                @Suppress("UNCHECKED_CAST")
                val urls = doc.get("urls") as? List<String> ?: emptyList()
                _banners.value = urls
            } catch (e: Exception) {
                _banners.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveBanners(urls: List<String>, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                _isSaving.value = true
                bannersDoc.set(mapOf("urls" to urls)).await()
                _banners.value = urls
                onResult(true, "Banners saved successfully")
            } catch (e: Exception) {
                onResult(false, e.localizedMessage ?: "Failed to save banners")
            } finally {
                _isSaving.value = false
            }
        }
    }
}