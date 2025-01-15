package com.ms.lcw

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.lcw.lsdk.data.requests.OmnichannelConfig
import com.lcw.lsdk.logger.OLog
import com.ms.lcw.script.ScriptAttributeExtractor

class Utility {

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("OCPrefs", Context.MODE_PRIVATE)
    }

    private fun getGson(): Gson {
        return Gson()
    }

    private fun <T> saveObjectToPreferences(context: Context, key: String, item: T) {
        val sharedPreferences = getSharedPreferences(context)
        val json = getGson().toJson(item)
        sharedPreferences.edit().putString(key, json).apply()
    }

    private fun <T> getObjectFromPreferences(context: Context, key: String, type: Class<T>): T? {
        val sharedPreferences = getSharedPreferences(context)
        val json = sharedPreferences.getString(key, null)
        return if (json != null) {
            getGson().fromJson(json, type)
        } else {
            null
        }
    }

    fun storeItem(context: Context, key: String, item: OmnichannelConfig) {
        saveObjectToPreferences(context, key, item)
    }

    fun storeAuth(context: Context, key: String, item: String) {
        val sharedPreferences = getSharedPreferences(context)
        sharedPreferences.edit().putString(key, item).apply()
    }

    fun retrieveItem(context: Context, key: String): OmnichannelConfig? {
        return getObjectFromPreferences(context, key, OmnichannelConfig::class.java)
    }

    fun getFCMToken(context: Context, key: String): String {
        return getSharedPreferences(context).getString(key, "") ?: ""
    }

    fun getAuth(context: Context, key: String): String {
        return getSharedPreferences(context).getString(key, "") ?: ""
    }

    fun extract(scriptTag: String?): OmnichannelConfig? {
        if (scriptTag.isNullOrEmpty()) return null

        val attributes = ScriptAttributeExtractor.extractAttributes(scriptTag)

        return attributes?.let {
            OmnichannelConfig(
                widgetId = it.dataAppId,
                orgId = it.dataOrgId,
                orgUrl = it.dataOrgUrl
            )
        } ?: run {
            OLog.d("No valid script attributes found.")
            null
        }
    }
}
