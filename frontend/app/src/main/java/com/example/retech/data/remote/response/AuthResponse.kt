package com.example.retech.data.remote.response

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val token: String?,
    val user: UserData?
)

data class UserData(
    val id: String,
    val name: String,
    val email: String
)