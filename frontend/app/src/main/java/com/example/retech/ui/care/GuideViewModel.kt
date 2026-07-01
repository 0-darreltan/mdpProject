package com.example.retech.ui.care

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.retech.data.local.AppDatabase
import com.example.retech.data.remote.GuideRepository
import com.example.retech.data.remote.RetrofitClient
import com.example.retech.databaseModel.Guide
import kotlinx.coroutines.launch

class GuideViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GuideRepository

    init {
        val guideDao = AppDatabase.getDatabase(application).guideDao()
        repository = GuideRepository(guideDao, RetrofitClient.guideService)
    }

    // Menggunakan LiveData dari local storage sebagai Single Source of Truth
    val guides: LiveData<List<Guide>> = repository.getLocalGuides()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun fetchGuides() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                // Refresh data dari remote lalu simpan ke local
                repository.refreshGuides()
            } catch (e: Exception) {
                Log.e("GuideViewModel", "Error fetching guides", e)
                _errorMessage.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
