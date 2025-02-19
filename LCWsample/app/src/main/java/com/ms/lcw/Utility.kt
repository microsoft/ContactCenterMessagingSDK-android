package com.ms.lcw

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lcw.lsdk.data.requests.OmnichannelConfig
import com.lcw.lsdk.logger.OLog
import com.ms.lcw.script.ScriptAttributeExtractor

class Utility {

    fun storeItem(context: Context, key: String, item: OmnichannelConfig) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("OCPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(item)
        editor.putString(key, json)
        editor.apply()
    }

    fun storeAuth(context: Context, key: String, item: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("OCPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, item)
        editor.apply()
    }

    fun storeFCMToken(context: Context, key: String, item: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("OCPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, item)
        editor.apply()
    }

    fun retrieveItem(context: Context, key: String): OmnichannelConfig? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("OCPrefs", Context.MODE_PRIVATE)
        val gson = Gson()

        // Retrieve the JSON string
        val json = sharedPreferences.getString(key, null)

        // Convert the JSON string back to the Item object
        return if (json != null) {
            val type = object : TypeToken<OmnichannelConfig>() {}.type
            gson.fromJson(json, type)
        } else {
            null
        }
    }

    fun getFCMToken(context: Context, key: String): String {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("OCPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, null) ?: ""
    }

    fun getAuth(context: Context, key: String): String {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("OCPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, null) ?: ""
    }

    fun clearOrgs(context: Context, key: String) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("OCPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(key)
        editor.apply()
    }
}
