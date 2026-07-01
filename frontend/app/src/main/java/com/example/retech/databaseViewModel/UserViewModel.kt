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

    val loginEmail = MutableLiveData<String>()
    val loginPassword = MutableLiveData<String>()

    val registerName = MutableLiveData<String>()
    val registerEmail = MutableLiveData<String>()
    val registerPassword = MutableLiveData<String>()

    fun register() {
        val name = registerName.value?.trim() ?: ""
        val email = registerEmail.value?.trim() ?: ""
        val password = registerPassword.value?.trim() ?: ""

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            _error.value = "Harap isi semua kolom"
            return
        }

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

    fun login() {
        val email = loginEmail.value?.trim() ?: ""
        val password = loginPassword.value?.trim() ?: ""
        
        if (email.isEmpty() || password.isEmpty()) {
            _error.value = "Harap isi email dan password"
            return
        }

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

    fun loginWithGoogle(name: String, email: String, profilePicture: String = "") {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val data = mapOf(
                    "name" to name,
                    "email" to email,
                    "profile_picture" to profilePicture
                )
                val response = RetrofitClient.userService.loginWithGoogle(data)
                if (response.isSuccessful) {
                    _authResult.value = response.body()
                } else {
                    _error.value = parseError(response.errorBody()?.string()) ?: "Login Google Failed"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private val _forgotPasswordSuccess = MutableLiveData<Boolean>()
    val forgotPasswordSuccess: LiveData<Boolean> get() = _forgotPasswordSuccess

    private val _resetSuccess = MutableLiveData<Boolean?>()
    val resetSuccess: LiveData<Boolean?> get() = _resetSuccess

    private val _changePasswordSuccess = MutableLiveData<Boolean?>()
    val changePasswordSuccess: LiveData<Boolean?> get() = _changePasswordSuccess

    val forgetPasswordEmail = MutableLiveData<String>()

    fun forgotPassword() {
        val email = forgetPasswordEmail.value?.trim() ?: ""
        if (email.isEmpty()) {
            _error.value = "Email tidak boleh kosong"
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val data = mapOf("email" to email)
                val response = RetrofitClient.userService.forgotPassword(data)
                if (response.isSuccessful) {
                    _forgotPasswordSuccess.value = true
                } else {
                    _error.value = parseError(response.errorBody()?.string()) ?: "Failed"
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

    val resetToken = MutableLiveData<String>()
    val resetNewPassword = MutableLiveData<String>()
    val resetConfirmPassword = MutableLiveData<String>()

    fun resetPassword() {
        val token = resetToken.value?.trim() ?: ""
        val newPassword = resetNewPassword.value?.trim() ?: ""
        val confirmPassword = resetConfirmPassword.value?.trim() ?: ""

        if (token.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            _error.value = "Semua bidang wajib diisi"
            return
        }

        if (newPassword.length < 6) {
            _error.value = "Password baru minimal 6 karakter"
            return
        }

        if (newPassword != confirmPassword) {
            _error.value = "Konfirmasi password tidak cocok"
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val data = mapOf("token" to token, "newPassword" to newPassword)
                val response = RetrofitClient.userService.resetPassword(data)
                if (response.isSuccessful) {
                    _resetSuccess.value = true
                } else {
                    _error.value = parseError(response.errorBody()?.string()) ?: "Failed"
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

    val changeOldPassword = MutableLiveData<String>()
    val changeNewPassword = MutableLiveData<String>()
    val changeConfirmPassword = MutableLiveData<String>()

    fun changePassword(email: String) {
        val oldPassword = changeOldPassword.value?.trim() ?: ""
        val newPassword = changeNewPassword.value?.trim() ?: ""
        val confirmPassword = changeConfirmPassword.value?.trim() ?: ""

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            _error.value = "Semua bidang wajib diisi"
            return
        }

        if (newPassword.length < 6) {
            _error.value = "Password baru minimal 6 karakter"
            return
        }

        if (newPassword != confirmPassword) {
            _error.value = "Konfirmasi password tidak cocok"
            return
        }

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
                    _error.value = parseError(response.errorBody()?.string()) ?: "Failed"
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

    private val _updateProfilePictureSuccess = MutableLiveData<Boolean?>()
    val updateProfilePictureSuccess: LiveData<Boolean?> get() = _updateProfilePictureSuccess

    fun updateProfilePicture(email: String, base64Image: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val data = mapOf(
                    "email" to email,
                    "profile_picture" to base64Image
                )
                val response = RetrofitClient.userService.updateProfilePicture(data)
                if (response.isSuccessful) {
                    _updateProfilePictureSuccess.value = true
                } else {
                    _error.value = parseError(response.errorBody()?.string()) ?: "Failed to update picture"
                    _updateProfilePictureSuccess.value = false
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                _updateProfilePictureSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetUpdateProfilePictureState() {
        _updateProfilePictureSuccess.value = null
    }

    private fun parseError(errorBody: String?): String? {
        return try {
            if (errorBody.isNullOrEmpty()) return null
            val jsonObject = org.json.JSONObject(errorBody)
            jsonObject.getString("message")
        } catch (e: Exception) {
            null
        }
    }
}
