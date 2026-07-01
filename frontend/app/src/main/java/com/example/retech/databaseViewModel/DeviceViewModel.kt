package com.example.retech.databaseViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.retech.data.local.AppDatabase
import com.example.retech.data.local.DeviceRepository
import com.example.retech.databaseModel.Device
import kotlinx.coroutines.launch

class DeviceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DeviceRepository = DeviceRepository(AppDatabase.getDatabase(application).deviceDao())
    private val _currentUserId = MutableLiveData<String>()

    // LiveData yang otomatis berubah ketika userId berubah
    val devices: LiveData<List<Device>> = _currentUserId.switchMap { userId ->
        repository.getDevicesByUserId(userId)
    }

    val totalDeviceCount: LiveData<Int> = _currentUserId.switchMap { userId ->
        repository.getDeviceCountByUserId(userId)
    }

    val recycleReadyCount: LiveData<Int> = _currentUserId.switchMap { userId ->
        repository.getRecycleReadyCountByUserId(userId)
    }

    // Status untuk insert
    private val _insertResult = MutableLiveData<Boolean>()
    val insertResult: LiveData<Boolean> get() = _insertResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    // Binding properties
    val newDeviceName = MutableLiveData<String>()

    // Set user ID yang sedang login, agar LiveData devices di-filter per user
    fun setUserId(userId: String) {
        _currentUserId.value = userId
    }

    // Insert device baru
    fun insertDevice(device: Device) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                repository.insertDevice(device)
                _insertResult.value = true
            } catch (e: Exception) {
                _error.value = "Gagal menyimpan device: ${e.message}"
                _insertResult.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Update device
    fun updateDevice(device: Device) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                repository.updateDevice(device)
                _insertResult.value = true
            } catch (e: Exception) {
                _error.value = "Gagal mengupdate device: ${e.message}"
                _insertResult.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Delete device
    fun deleteDevice(device: Device) {
        viewModelScope.launch {
            try {
                repository.deleteDevice(device)
            } catch (e: Exception) {
                _error.value = "Gagal menghapus device: ${e.message}"
            }
        }
    }

    // Delete device berdasarkan ID
    fun deleteDeviceById(deviceId: Int) {
        viewModelScope.launch {
            try {
                repository.deleteDeviceById(deviceId)
            } catch (e: Exception) {
                _error.value = "Gagal menghapus device: ${e.message}"
            }
        }
    }

    // Reset insert result setelah di-observe
    fun resetInsertResult() {
        _insertResult.value = false
    }
}
