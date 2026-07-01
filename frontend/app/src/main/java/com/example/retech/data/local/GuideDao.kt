package com.example.retech.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.retech.databaseModel.Guide

@Dao
interface GuideDao {
    @Query("SELECT * FROM guides")
    fun getAllGuides(): LiveData<List<Guide>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuides(guides: List<Guide>)

    @Query("DELETE FROM guides")
    suspend fun clearGuides()
}
