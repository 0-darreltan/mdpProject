package com.example.retech.databaseViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retech.data.remote.RetrofitClient
import com.example.retech.databaseModel.AuthResponse
import com.example.retech.databaseModel.Users
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val _authResult = MutableLiveData<AuthResponse?>()
    val authResult: LiveData<AuthResponse?> get() = _authResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun register(name: String, email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val user = Users(name = name, email = email, password = password)
                val response = RetrofitClient.userService.register(user)
                if (response.isSuccessful) {
                    _authResult.value = response.body()
                } else {
                    _error.value = "Registration Failed: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val credentials = mapOf("email" to email, "password" to password)
                val response = RetrofitClient.userService.login(credentials)
                if (response.isSuccessful) {
                    _authResult.value = response.body()
                } else {
                    _error.value = "Login Failed: Invalid credentials"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loginWithGoogle(name: String, email: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val googleData = mapOf("name" to name, "email" to email)
                val response = RetrofitClient.userService.loginWithGoogle(googleData)
                if (response.isSuccessful) {
                    _authResult.value = response.body()
                } else {
                    _error.value = "Google Login Failed"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
