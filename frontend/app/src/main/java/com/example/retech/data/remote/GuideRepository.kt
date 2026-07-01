package com.example.retech.data.remote

import androidx.lifecycle.LiveData
import com.example.retech.data.local.GuideDao
import com.example.retech.databaseApi.GuideApiService
import com.example.retech.databaseModel.Guide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GuideRepository(
    private val guideDao: GuideDao,
    private val apiService: GuideApiService
) {

    // Ambil data dari local (Single Source of Truth)
    fun getLocalGuides(): LiveData<List<Guide>> {
        return guideDao.getAllGuides()
    }

    // Refresh data dari remote ke local
    suspend fun refreshGuides() {
        withContext(Dispatchers.IO) {
            try {
                val remoteGuides = apiService.getGuides()
                if (remoteGuides.isNotEmpty()) {
                    guideDao.clearGuides()
                    guideDao.insertGuides(remoteGuides)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle error (e.g. no internet), local data is still served
            }
        }
    }
}
