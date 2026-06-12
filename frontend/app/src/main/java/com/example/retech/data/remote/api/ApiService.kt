package com.example.retech.data.remote.api

import com.example.retech.data.remote.response.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/auth/google")
    suspend fun loginRegisterWithGoogle(
        @Body request: GoogleAuthRequest
    ): Response<AuthResponse>

}