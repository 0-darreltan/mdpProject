package com.example.retech.databaseApi

import com.example.retech.databaseModel.Guide
import retrofit2.http.GET

interface GuideApiService {
    @GET("api/guides")
    suspend fun getGuides(): List<Guide>
}