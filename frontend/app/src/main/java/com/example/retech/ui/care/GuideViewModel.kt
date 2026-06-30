package com.example.retech.ui.care

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retech.data.remote.RetrofitClient
import com.example.retech.databaseModel.Guide
import kotlinx.coroutines.launch

class GuideViewModel : ViewModel() {

    private val _guides = MutableLiveData<List<Guide>>()
    val guides: LiveData<List<Guide>> get() = _guides

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun fetchGuides() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = RetrofitClient.guideService.getGuides()
                _guides.value = response
            } catch (e: Exception) {
                Log.e("GuideViewModel", "Error fetching guides", e)
                _errorMessage.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
