package com.software.app.update.smart.Activity

import android.app.AppOpsManager
import android.app.usage.StorageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.storage.StorageManager
import android.provider.Settings
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.software.app.update.smart.Adapter.AppCacheAdapter
import com.software.app.update.smart.Model.AppCacheModel
import com.software.app.update.smart.R
import kotlin.concurrent.thread

class JunkAppsActivity : BaseActivity() {

    private lateinit var imgBack: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppCacheAdapter
    private val appList = mutableListOf<AppCacheModel>()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_junk_apps)

        imgBack = findViewById(R.id.imgBack)
        recyclerView = findViewById(R.id.recyclerView)
        adapter = AppCacheAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter


        imgBack.setOnClickListener { onBackPressed() }

        if (!hasUsageStatsPermission()) {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            finish()
        } else {
            loadAppCacheSizes()
        }


    }


    private fun hasUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadAppCacheSizes() {
        val pm = packageManager
        val storageStatsManager =
            getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        thread {
            val list = mutableListOf<AppCacheModel>()
            for (app in apps) {
                try {
                    val appInfo = pm.getApplicationInfo(app.packageName, 0)
                    val storageStats = storageStatsManager.queryStatsForPackage(
                        StorageManager.UUID_DEFAULT,
                        app.packageName,
                        android.os.Process.myUserHandle()
                    )
                    val cacheSize = storageStats.cacheBytes
                    if (cacheSize > 0L) {
                        list.add(
                            AppCacheModel(
                                app.loadLabel(pm).toString(),
                                app.packageName,
                                cacheSize,
                                app.loadIcon(pm)
                            )
                        )
                    }
                } catch (_: Exception) {
                }
            }

            runOnUiThread {
                adapter.submitList(list.sortedByDescending { it.cacheSize })
            }
        }
    }
}