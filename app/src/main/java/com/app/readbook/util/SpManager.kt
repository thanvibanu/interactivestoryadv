package com.app.readbook.util

import android.content.Context
import com.app.readbook.App.Companion.user
import com.app.readbook.data.User
import com.google.gson.Gson

object SpManager {
    private const val PREF_NAME = "password_pref"
    private const val KEY_PASSWORD = "password_key"
    private const val KEY_USERNAME = "username_key"
    private const val KEY_USER = "Data"

    @JvmStatic
    fun savePassword(context: Context, password: String?) {
        val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(KEY_PASSWORD, password)
        editor.apply()
    }

    @JvmStatic
    fun getPassword(context: Context): String? {
        val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return preferences.getString(KEY_PASSWORD, "")
    }

    @JvmStatic
    fun clearPassword(context: Context) {
        val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.remove(KEY_PASSWORD)
        editor.apply()
    }

    @JvmStatic
    fun saveUsername(context: Context, username: String?) {
        val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(KEY_USERNAME, username)
        editor.apply()
    }

    @JvmStatic
    fun getUsername(context: Context): String? {
        val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return preferences.getString(KEY_USERNAME, "")
    }

    @JvmStatic
    fun clearUsername(context: Context) {
        val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.remove(KEY_USERNAME)
        editor.apply()
    }

    fun saveUserData(context: Context, userData: User?) {
        if (userData == null) return
        val preferences = context.getSharedPreferences(KEY_USER, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        val gson = Gson()
        val json = gson.toJson(user)
        editor.putString("user", json)
        editor.apply()
    }

    fun getUserData(context: Context?): User? {
        val preferences = context?.getSharedPreferences(KEY_USER, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = preferences?.getString("user", "")
        return if (json.isNullOrEmpty()) {
            null
        } else {
            gson.fromJson(json, User::class.java)
        }
    }
}