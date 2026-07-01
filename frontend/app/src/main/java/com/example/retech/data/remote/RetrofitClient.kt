package com.example.retech.data.remote

import com.example.retech.data.remote.api.ApiService
import com.example.retech.databaseApi.GuideApiService
import com.example.retech.databaseApi.LocationApiService
import com.example.retech.databaseApi.UserApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://backend-eight-fawn-38.vercel.app/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
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
