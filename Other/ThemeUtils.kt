package com.software.app.update.smart.Other

import android.app.Activity
import android.content.Context
import com.software.app.update.smart.R

object ThemeUtils {

    const val PREFS_NAME = "theme_prefs"
    const val KEY_THEME = "theme_key"

    const val   THEME_LIGHT = "light"
    const val THEME_DARK = "dark"

    fun applyTheme(activity: Activity) {
        when (getSavedTheme(activity)) {
            THEME_DARK -> activity.setTheme(R.style.Theme_MyApp_Dark)
            else -> activity.setTheme(R.style.Theme_MyApp_Light)
        }
    }

    fun saveTheme(context: Context, theme: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_THEME, theme).apply()
    }

    fun getSavedTheme(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_THEME, THEME_LIGHT) ?: THEME_LIGHT
    }
}