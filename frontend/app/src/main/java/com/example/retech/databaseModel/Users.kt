package com.example.retech.databaseModel

import java.io.Serializable
import com.google.gson.annotations.SerializedName

data class Users(
    @SerializedName("_id", alternate = ["id"])
    val _id: String? = null,
    val name: String,
    val email: String,
    val password: String? = null,
    val auth_provider: String? = "manual",
    val profile_picture: String? = "",
    val role: String? = "user"
) : Serializable

data class AuthResponse(
    val success: Boolean,
    val message: String?,
    val token: String?,
    val user: Users?
)
