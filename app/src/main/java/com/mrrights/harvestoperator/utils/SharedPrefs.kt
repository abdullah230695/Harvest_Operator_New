package com.mrrights.harvestoperator.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPrefs(context: Context, prefsName: String) {


    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

    //saveString
    fun saveString(KEY_NAME: String, value: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(KEY_NAME, value)
        editor.apply()
    }

    //saveInt
    fun saveInt(KEY_NAME: String, value: Int) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putInt(KEY_NAME, value)
        editor.apply()
    }

    //saveBoolean
    fun saveBoolean(KEY_NAME: String, status: Boolean) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean(KEY_NAME, status)
        editor.apply()
    }

    //saveLong
    fun saveLong(KEY_NAME: String, status: Long) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putLong(KEY_NAME, status)
        editor.apply()
    }


    //saveFloat
    fun saveFloat(KEY_NAME: String, status: Float) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putFloat(KEY_NAME, status)
        editor.apply()
    }

    //getString
    fun getString(KEY_NAME: String): String? {
        return sharedPreferences.getString(KEY_NAME, null)
    }

    //getInt
    fun getInt(KEY_NAME: String): Int {
        return sharedPreferences.getInt(KEY_NAME, 0)
    }

    //getBoolean
    fun getBoolean(KEY_NAME: String): Boolean? {
        return sharedPreferences.getBoolean(KEY_NAME, false)
    }

    //getLong
    fun getLong(KEY_NAME: String): Long {
        return sharedPreferences.getLong(KEY_NAME, 0)
    }

    //getLong
    fun getFloat(KEY_NAME: String): Float {
        return sharedPreferences.getFloat(KEY_NAME, 0F)
    }

    //removeSpecific
    fun removeValue(KEY_NAME: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.remove(KEY_NAME)
        editor.apply()
    }

    //clearAll
    fun clearSharedPreference() {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }


}