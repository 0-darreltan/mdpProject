package com.example.retech.databaseApi

import com.example.retech.databaseModel.Locations
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface LocationApiService {
    @GET("api/locations")
    suspend fun getAllLocations(): Response<List<Locations>>

    @POST("api/locations")
    suspend fun addLocation(@Body location: Locations): Response<Locations>

    @PUT("api/locations/{id}")
    suspend fun updateLocation(@Path("id") id: String, @Body location: Locations): Response<Locations>

    @DELETE("api/locations/{id}")
    suspend fun deleteLocation(@Path("id") id: String): Response<Unit>
}
