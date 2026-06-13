package com.example.retech.databaseModel

import java.io.Serializable

data class Users(
    val _id: String? = null,
    val name: String,
    val email: String,
    val password: String? = null,
    val auth_provider: String? = "manual"
) : Serializable

data class AuthResponse(
    val success: Boolean,
    val message: String?,
    val token: String?,
    val user: Users?
)
