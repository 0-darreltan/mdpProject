package com.example.retech.databaseViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retech.data.remote.RetrofitClient
import com.example.retech.databaseModel.AuthResponse
import com.example.retech.databaseModel.Users
import kotlinx.coroutines.launch
import org.json.JSONObject

class UserViewModel : ViewModel() {

    private val _authResult = MutableLiveData<AuthResponse?>()
    val authResult: LiveData<AuthResponse?> get() = _authResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    // Status khusus untuk Reset Password
    private val _resetSuccess = MutableLiveData<Boolean>()
    val resetSuccess: LiveData<Boolean> get() = _resetSuccess

    // Status khusus untuk Forget Password
    private val _forgotPasswordSuccess = MutableLiveData<Boolean>()
    val forgotPasswordSuccess: LiveData<Boolean> get() = _forgotPasswordSuccess

    fun register(name: String, email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val user = Users(name = name, email = email, password = password)
                val response = RetrofitClient.userService.register(user)
                if (response.isSuccessful) {
                    _authResult.value = response.body()
                } else {
                    _error.value = parseError(response.errorBody()?.string()) ?: "Registration Failed"
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
                    _error.value = parseError(response.errorBody()?.string()) ?: "Login Failed"
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
        val googleUser = Users(_id = "google_${email}", name = name, email = email, auth_provider = "google")
        val response = AuthResponse(success = true, message = "Login Google Sukses", token = "local_token", user = googleUser)
        _authResult.value = response
        _isLoading.value = false
    }

    fun forgotPassword(email: String) {
        _isLoading.value = true
        _forgotPasswordSuccess.value = false
        viewModelScope.launch {
            try {
                val body = mapOf("email" to email)
                val response = RetrofitClient.userService.forgotPassword(body)
                if (response.isSuccessful) {
                    _forgotPasswordSuccess.value = true
                    _error.value = response.body()?.message ?: "Link reset terkirim"
                } else {
                    val errorMsg = parseError(response.errorBody()?.string())
                    _error.value = errorMsg ?: "Gagal: Pastikan backend sudah menyala"
                }
            } catch (e: Exception) {
                _error.value = "Koneksi Gagal: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetPassword(token: String, newPassword: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val body = mapOf("token" to token, "newPassword" to newPassword)
                val response = RetrofitClient.userService.resetPassword(body)
                if (response.isSuccessful) {
                    _resetSuccess.value = true
                    _error.value = response.body()?.message ?: "Password berhasil diubah"
                } else {
                    _error.value = parseError(response.errorBody()?.string()) ?: "Gagal reset password"
                }
            } catch (e: Exception) {
                _error.value = "Koneksi Gagal: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun parseError(json: String?): String? {
        return try {
            if (json == null) return null
            val obj = JSONObject(json)
            obj.getString("message")
        } catch (e: Exception) {
            null
        }
    }
}
