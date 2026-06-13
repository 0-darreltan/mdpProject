package com.example.retech.databaseModel

import java.io.Serializable

data class Locations(
    val _id: String? = null,
    val name: String,
    val address: String,
    val accepted_items: List<String>,
    val operational_hours: List<OperationalHour>,
    val latitude: Double,
    val longitude: Double,
    val image_url: String? = null
) : Serializable

data class OperationalHour(
    val days: String,
    val time: String
) : Serializable
