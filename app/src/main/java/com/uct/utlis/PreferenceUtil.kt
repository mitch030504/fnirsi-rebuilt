package com.uct.utlis

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil private constructor(context: Context) {
    private val sharedPreference: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun getConfig(defaultValue: String): String = sharedPreference.getString(CONFIG, defaultValue) ?: defaultValue

    fun setConfig(value: String) {
        sharedPreference.edit().putString(CONFIG, value).commit()
    }

    fun getLang(defaultValue: String): String = sharedPreference.getString(LANG, defaultValue) ?: defaultValue

    fun setLang(value: String) {
        sharedPreference.edit().putString(LANG, value).commit()
    }

    fun getRemember(defaultValue: String): String =
        sharedPreference.getString(Remember, defaultValue) ?: defaultValue

    fun setRemember(value: String) {
        sharedPreference.edit().putString(Remember, value).commit()
    }

    fun getDydy(defaultValue: String): String = sharedPreference.getString(dydy, defaultValue) ?: defaultValue

    fun getDydyfalg(defaultValue: Boolean): Boolean = sharedPreference.getBoolean(dydyfalg, defaultValue)

    fun setDydy(value: String) {
        sharedPreference.edit().putString(dydy, value).commit()
    }

    fun setDydyfalg(value: Boolean) {
        sharedPreference.edit().putBoolean(dydyfalg, value).commit()
    }

    fun setZdfalg(value: Boolean) {
        sharedPreference.edit().putBoolean(zdfalg, value).commit()
    }

    fun getZdfalg(defaultValue: Boolean): Boolean = sharedPreference.getBoolean(zdfalg, defaultValue)

    companion object {
        const val CONFIG = "CONFIG"
        const val LANG = "Lang"
        const val LANG1 = "Lang1"
        const val Remember = "Remember"
        const val dydy = "dydy"
        const val dydyfalg = "dydyfalg"
        const val zdfalg = "zdfalg"
        private const val PREFERENCES_NAME = "_preferences"

        @Volatile
        private var preference: PreferenceUtil? = null

        @JvmStatic
        fun getInstance(context: Context): PreferenceUtil {
            return preference ?: synchronized(this) {
                preference ?: PreferenceUtil(context).also { preference = it }
            }
        }
    }
}
