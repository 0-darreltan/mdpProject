package com.example.retech.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.retech.databaseModel.Device

@Dao
interface DeviceDao {

    // Ambil semua device milik user tertentu
    @Query("SELECT * FROM devices WHERE userId = :userId ORDER BY createdAt DESC")
    fun getDevicesByUserId(userId: String): LiveData<List<Device>>

    // Hitung total device milik user
    @Query("SELECT COUNT(*) FROM devices WHERE userId = :userId")
    fun getDeviceCountByUserId(userId: String): LiveData<Int>

    // Hitung device yang badge-nya "Recycle Ready" milik user
    @Query("SELECT COUNT(*) FROM devices WHERE userId = :userId AND badge = 'Recycle Ready'")
    fun getRecycleReadyCountByUserId(userId: String): LiveData<Int>

    // Insert device baru
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: Device)

    // Update device
    @Update
    suspend fun updateDevice(device: Device)

    // Delete device
    @Delete
    suspend fun deleteDevice(device: Device)

    // Delete device berdasarkan ID
    @Query("DELETE FROM devices WHERE id = :deviceId")
    suspend fun deleteDeviceById(deviceId: Int)

    // Ambil satu device berdasarkan ID
    @Query("SELECT * FROM devices WHERE id = :deviceId LIMIT 1")
    suspend fun getDeviceById(deviceId: Int): Device?
}
