package com.software.app.update.smart.Activity

import android.app.AppOpsManager
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.software.app.update.smart.Adapter.AppUsageAdapter
import com.software.app.update.smart.Model.AppUsageModel
import com.software.app.update.smart.Other.LocaleHelper
import com.software.app.update.smart.Other.PreferencesHelper11
import com.software.app.update.smart.R


class AppUsageActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var imgBack: ImageView

    override fun attachBaseContext(newBase: Context) {
        val langCode = PreferencesHelper11(newBase).selectedLanguage ?: "en"
        super.attachBaseContext(LocaleHelper.setLocale(newBase, langCode))
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_usage)

        imgBack = findViewById(R.id.imgBack)
        imgBack.setOnClickListener { onBackPressed() }
        recyclerView = findViewById(R.id.recyclerView)

        if (!hasUsageAccess()) {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            finish()
        } else {
            loadUsageStats()
        }
    }

    private fun hasUsageAccess(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(), packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun loadUsageStats() {
        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val networkStatsManager =
            getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
        val pm = packageManager

        val endTime = System.currentTimeMillis()
        val startTime = endTime - 1000L * 60 * 60 * 24

        val usageStats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        ).filter { it.totalTimeInForeground > 0 }

        val usageEvents = usageStatsManager.queryEvents(startTime, endTime)
        val launchCounts = mutableMapOf<String, Int>()

        val event = UsageEvents.Event()
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                launchCounts[event.packageName] = (launchCounts[event.packageName] ?: 0) + 1
            }
        }

        val apps = usageStats.mapNotNull {
            try {
                val appInfo = pm.getApplicationInfo(it.packageName, 0)
                val uid = appInfo.uid

                val dataBucket = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_WIFI,
                    null,
                    startTime,
                    endTime,
                    uid
                )

                var totalBytes = 0L
                val bucket = NetworkStats.Bucket()
                while (dataBucket.hasNextBucket()) {
                    dataBucket.getNextBucket(bucket)
                    totalBytes += bucket.rxBytes + bucket.txBytes
                }

                AppUsageModel(
                    appName = pm.getApplicationLabel(appInfo).toString(),
                    packageName = it.packageName,
                    icon = pm.getApplicationIcon(appInfo),
                    timeSpent = it.totalTimeInForeground,
                    launchCount = launchCounts[it.packageName] ?: 0,
                    lastUsed = it.lastTimeUsed,
                    dataUsedBytes = totalBytes
                )
            } catch (e: Exception) {
                null
            }
        }.sortedByDescending { it.timeSpent }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = AppUsageAdapter(apps)
    }
}