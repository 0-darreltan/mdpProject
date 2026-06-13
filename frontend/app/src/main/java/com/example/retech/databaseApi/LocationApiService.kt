package com.example.retech.databaseApi

import com.example.retech.databaseModel.Locations
import retrofit2.Response
import retrofit2.http.GET

interface LocationApiService {
    @GET("api/locations")
    suspend fun getAllLocations(): Response<List<Locations>>
}
