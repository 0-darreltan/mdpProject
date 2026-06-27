package com.example.retech.databaseModel

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "devices")
data class Device(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,       // ID user pemilik device
    val name: String,
    val category: String,
    val purchaseYear: Int,
    val condition: String = "Good",
    val badge: String = "High Value",
    val createdAt: Long = System.currentTimeMillis()
) : Serializable
