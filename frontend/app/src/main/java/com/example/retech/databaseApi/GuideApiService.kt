package com.example.retech.databaseApi

import com.example.retech.databaseModel.Guide
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface GuideApiService {
    @GET("api/guides")
    suspend fun getGuides(): List<Guide>

    @POST("api/guides")
    suspend fun addGuide(@Body guide: Guide): Response<Guide>

    @PUT("api/guides/{id}")
    suspend fun updateGuide(@Path("id") id: String, @Body guide: Guide): Response<Guide>

    @DELETE("api/guides/{id}")
    suspend fun deleteGuide(@Path("id") id: String): Response<Unit>
}