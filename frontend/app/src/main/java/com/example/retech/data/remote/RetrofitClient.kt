package com.example.retech.data.remote

import com.example.retech.data.remote.api.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // ⚠️ ALAMAT IP BACKEND KAMU ⚠️
    // Jika kamu testing pakai Emulator Android Studio dan server Express-nya nyala di laptop yang sama,
    // Android mendeteksi localhost laptop lewat IP khusus: "http://10.0.2.2:5000/" (Sesuaikan portnya, misal 5000)
    private const val BASE_URL = "http://10.0.2.2:3000/"

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}