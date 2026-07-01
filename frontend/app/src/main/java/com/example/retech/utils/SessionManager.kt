package com.example.retech.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("RETECH_PREFS", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USER_ID = "userId"
        private const val KEY_USER_NAME = "userName"
        private const val KEY_USER_EMAIL = "userEmail"
        private const val KEY_PROFILE_PICTURE = "profilePicture"
        private const val KEY_USER_ROLE = "userRole"
    }

    fun saveSession(userId: String, name: String, email: String, profilePicture: String? = "", role: String? = "user") {
        val editor = prefs.edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putString(KEY_USER_ID, userId)
        editor.putString(KEY_USER_NAME, name)
        editor.putString(KEY_USER_EMAIL, email)
        editor.putString(KEY_PROFILE_PICTURE, profilePicture)
        editor.putString(KEY_USER_ROLE, role)
        editor.apply()
    }

    fun saveProfilePicture(profilePicture: String?) {
        val editor = prefs.edit()
        editor.putString(KEY_PROFILE_PICTURE, profilePicture)
        editor.apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)

    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, null)

    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)

    fun getProfilePicture(): String? = prefs.getString(KEY_PROFILE_PICTURE, "")

    fun getUserRole(): String? = prefs.getString(KEY_USER_ROLE, "user")

    fun logout() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}