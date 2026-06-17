package com.app.fishpoint.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.fishpoint.data.api.FishPointApi
import com.app.fishpoint.data.model.FishingSpot
import com.app.fishpoint.data.repository.FishPointRepository
import kotlinx.coroutines.launch

class SpotViewModel : ViewModel() {

    private val repository = FishPointRepository(FishPointApi.create())

    var spots by mutableStateOf<List<FishingSpot>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    init { fetchSpots() }

    fun fetchSpots() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val result = repository.getSpots()
            if (result.isSuccess) spots = result.getOrNull() ?: emptyList()
            else errorMessage = result.exceptionOrNull()?.message ?: "Gagal mengambil data spot"
            isLoading = false
        }
    }

    fun addSpot(
        context: android.content.Context, userId: Int, name: String, waterType: String, latitude: Double, longitude: Double, targetFish: String, description: String, photos: List<String>, onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            val result = repository.addSpot(context, userId, name, waterType, latitude, longitude, targetFish, description, photos)
            if (result.isSuccess) {
                fetchSpots()
                onSuccess()
            } else errorMessage = result.exceptionOrNull()?.message
            isLoading = false
        }
    }

    fun updateSpot(
        context: android.content.Context, spotId: Int, userId: Int, name: String, waterType: String, latitude: Double, longitude: Double, targetFish: String, description: String, photos: List<String>, onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            val result = repository.updateSpot(context, spotId, userId, name, waterType, latitude, longitude, targetFish, description, photos)
            if (result.isSuccess) {
                fetchSpots()
                onSuccess()
            } else errorMessage = result.exceptionOrNull()?.message
            isLoading = false
        }
    }

    fun addReview(
        spotId: Int, userId: Int, rating: Int, comment: String, onSuccess: () -> Unit, onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            val result = repository.addReview(spotId, userId, rating, comment)
            if (result.isSuccess) {
                fetchSpots()
                onSuccess()
            } else onError(result.exceptionOrNull()?.message ?: "Gagal menambah ulasan")
            isLoading = false
        }
    }

    fun deleteSpot(spotId: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            val result = repository.deleteSpot(spotId)
            if (result.isSuccess) {
                fetchSpots()
                onSuccess()
            } else onError(result.exceptionOrNull()?.message ?: "Gagal menghapus spot")
            isLoading = false
        }
    }

    fun deleteReview(reviewId: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            val result = repository.deleteReview(reviewId)
            if (result.isSuccess) {
                fetchSpots()
                onSuccess()
            } else onError(result.exceptionOrNull()?.message ?: "Gagal menghapus ulasan")
            isLoading = false
        }
    }
}