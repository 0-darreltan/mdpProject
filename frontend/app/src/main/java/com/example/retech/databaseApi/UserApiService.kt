package com.example.retech.databaseApi

import com.example.retech.databaseModel.AuthResponse
import com.example.retech.databaseModel.Users
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApiService {
    @POST("api/auth/register")
    suspend fun register(@Body user: Users): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body credentials: Map<String, String>): Response<AuthResponse>

    @POST("api/auth/google")
    suspend fun loginWithGoogle(@Body googleData: Map<String, String>): Response<AuthResponse>

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body emailData: Map<String, String>): Response<AuthResponse>

    @POST("api/auth/reset-password")
    suspend fun resetPassword(@Body resetData: Map<String, String>): Response<AuthResponse>

    @POST("api/auth/change-password")
    suspend fun changePassword(@Body changeData: Map<String, String>): Response<AuthResponse>
}
