package com.uct.utlis

import android.content.Context
import android.content.SharedPreferences

class SpUtil private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.applicationContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)

    fun putString(key: String, value: String?) {
        sharedPreferences.edit().putString(key, value).commit()
    }

    fun getString(key: String): String = sharedPreferences.getString(key, "") ?: ""

    companion object {
        const val LANGUAGE = "language"
        private const val SP_NAME = "poemTripSpref"

        @Volatile
        private var instance: SpUtil? = null

        @JvmStatic
        fun getInstance(context: Context): SpUtil {
            return instance ?: synchronized(this) {
                instance ?: SpUtil(context).also { instance = it }
            }
        }
    }
}
