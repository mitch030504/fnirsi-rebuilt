package com.uct.utlis

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import java.util.Locale

object LanguageUtil {
    private const val TAG = "LanguageUtil"

    @JvmStatic
    fun changeAppLanguage(context: Context, language: String?) {
        if (language.isNullOrEmpty()) {
            return
        }
        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration
        configuration.setLocale(getLocaleByLanguage(language))
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    @JvmStatic
    fun getLocaleByLanguage(language: String?): Locale {
        if (language.isNullOrEmpty()) {
            return Locale.SIMPLIFIED_CHINESE
        }
        val locale =
            when (language) {
                LanguageType.CHINESE.language -> Locale.SIMPLIFIED_CHINESE
                LanguageType.ENGLISH.language -> Locale.ENGLISH
                LanguageType.THAILAND.language,
                LanguageType.HW.language,
                LanguageType.FT.language,
                LanguageType.RU.language,
                LanguageType.JA.language,
                LanguageType.PT.language,
                LanguageType.FY.language,
                LanguageType.XBY.language,
                LanguageType.DY.language,
                -> Locale.forLanguageTag(language)

                else -> Locale.SIMPLIFIED_CHINESE
            }
        Log.d(TAG, "getLocaleByLanguage: ${locale.displayName}")
        return locale
    }

    @JvmStatic
    fun attachBaseContext(context: Context, language: String?): Context {
        Log.d(TAG, "attachBaseContext: ${Build.VERSION.SDK_INT}")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context, language)
        } else {
            context
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateResources(context: Context, language: String?): Context {
        val resources = context.resources
        val locale = getLocaleByLanguage(language)
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)
        configuration.setLocales(LocaleList(locale))
        return context.createConfigurationContext(configuration)
    }
}
