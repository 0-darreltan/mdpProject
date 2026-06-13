package com.example.retech.databaseViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retech.data.remote.RetrofitClient
import com.example.retech.databaseModel.Locations
import kotlinx.coroutines.launch

class LocationViewModel : ViewModel() {

    private val _locations = MutableLiveData<List<Locations>>()
    val locations: LiveData<List<Locations>> get() = _locations

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun fetchAllLocations() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitClient.locationService.getAllLocations()
                if (response.isSuccessful) {
                    _locations.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Failed to fetch locations: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
