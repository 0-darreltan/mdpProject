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

    private val _forgotPasswordSuccess = MutableLiveData<Boolean>()
    val forgotPasswordSuccess: LiveData<Boolean> get() = _forgotPasswordSuccess

    private val _resetSuccess = MutableLiveData<Boolean?>()
    val resetSuccess: LiveData<Boolean?> get() = _resetSuccess

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
        
        val googleUser = Users(
            _id = "google_${email}",
            name = name,
            email = email,
            auth_provider = "google"
        )
        
        val response = AuthResponse(
            success = true,
            message = "Login Google Sukses (Lokal)",
            token = "local_google_token",
            user = googleUser
        )

        // Set value langsung tanpa memanggil Retrofit
        _authResult.value = response
        _isLoading.value = false
    }

    fun forgotPassword(email: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val data = mapOf("email" to email)
                val response = RetrofitClient.userService.forgotPassword(data)
                if (response.isSuccessful) {
                    _forgotPasswordSuccess.value = true
                } else {
                    _error.value = "Failed: ${response.message()}"
                    _forgotPasswordSuccess.value = false
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                _forgotPasswordSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetPassword(token: String, newPassword: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val data = mapOf("token" to token, "newPassword" to newPassword)
                val response = RetrofitClient.userService.resetPassword(data)
                if (response.isSuccessful) {
                    _resetSuccess.value = true
                } else {
                    _error.value = "Failed: ${response.message()}"
                    _resetSuccess.value = false
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                _resetSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private val _changePasswordSuccess = MutableLiveData<Boolean?>()
    val changePasswordSuccess: LiveData<Boolean?> get() = _changePasswordSuccess

    fun changePassword(email: String, oldPassword: String, newPassword: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val data = mapOf(
                    "email" to email,
                    "oldPassword" to oldPassword,
                    "newPassword" to newPassword
                )
                val response = RetrofitClient.userService.changePassword(data)
                if (response.isSuccessful) {
                    _changePasswordSuccess.value = true
                } else {
                    _error.value = "Failed: ${response.message()}"
                    _changePasswordSuccess.value = false
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                _changePasswordSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun resetChangePasswordState() {
        _changePasswordSuccess.value = null
    }
}
