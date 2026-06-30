package com.example.retech.data.remote

import com.example.retech.data.remote.api.ApiService
import com.example.retech.databaseApi.GuideApiService
import com.example.retech.databaseApi.LocationApiService
import com.example.retech.databaseApi.UserApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:3000/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val instance: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    val userService: UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }

    val locationService: LocationApiService by lazy {
        retrofit.create(LocationApiService::class.java)
    }

    val guideService: GuideApiService by lazy {
        retrofit.create(GuideApiService::class.java)
    }
}
