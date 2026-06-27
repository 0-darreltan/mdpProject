package com.example.retech.data.local

import androidx.lifecycle.LiveData
import com.example.retech.databaseModel.Device

class DeviceRepository(private val deviceDao: DeviceDao) {

    // Ambil semua device milik user tertentu
    fun getDevicesByUserId(userId: String): LiveData<List<Device>> {
        return deviceDao.getDevicesByUserId(userId)
    }

    // Hitung total device milik user
    fun getDeviceCountByUserId(userId: String): LiveData<Int> {
        return deviceDao.getDeviceCountByUserId(userId)
    }

    // Hitung device "Recycle Ready" milik user
    fun getRecycleReadyCountByUserId(userId: String): LiveData<Int> {
        return deviceDao.getRecycleReadyCountByUserId(userId)
    }

    // Insert device baru
    suspend fun insertDevice(device: Device) {
        deviceDao.insertDevice(device)
    }

    // Update device
    suspend fun updateDevice(device: Device) {
        deviceDao.updateDevice(device)
    }

    // Delete device
    suspend fun deleteDevice(device: Device) {
        deviceDao.deleteDevice(device)
    }

    // Delete berdasarkan ID
    suspend fun deleteDeviceById(deviceId: Int) {
        deviceDao.deleteDeviceById(deviceId)
    }

    // Ambil device berdasarkan ID
    suspend fun getDeviceById(deviceId: Int): Device? {
        return deviceDao.getDeviceById(deviceId)
    }
}
