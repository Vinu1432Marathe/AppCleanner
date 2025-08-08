package com.software.app.update.smart.Other

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object SharePref {

    private const val PREF_NAME = "FixApps_Downloader"
    private const val VIDEO_LIST_KEY = "video_list"

    //todo Function to save a video in SharedPreferences


    private const val KEY_AppShow = "AppShow"
    private const val KEY_Rate = "AppRate"
    internal fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun setOnboarding(context: Context, accepted: Boolean) {
        getSharedPreferences(context).edit()
            .putBoolean(KEY_AppShow, accepted)
            .apply()
    }

    // Check if the terms are accepted
    fun isOnboarding(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_AppShow, false)
    }

    fun setRate(context: Context, accepted: Boolean) {
        getSharedPreferences(context).edit() {
            putBoolean(KEY_Rate, accepted)
        }
    }

    // Check if the terms are accepted
    fun isRate(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_Rate, false)
    }


}