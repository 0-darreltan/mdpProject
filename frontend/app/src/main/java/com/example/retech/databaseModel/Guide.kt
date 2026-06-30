package com.example.retech.databaseModel

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "guides")
data class Guide(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val category: String,
    val summary: String,
    val image_url: String,
    val file_url: String
) : Serializable



