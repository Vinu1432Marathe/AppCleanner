package com.software.app.update.smart.Activity

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.software.app.update.smart.Adapter.AppListAdapter
import com.software.app.update.smart.Adapter.AppListAdapter11
import com.software.app.update.smart.Model.AppInfoModel
import com.software.app.update.smart.Model.AppInfoModel11
import com.software.app.update.smart.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

@RequiresApi(Build.VERSION_CODES.O)
class DeviceInfoActivity : BaseActivity() {
    lateinit var imgBack: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppListAdapter11
    private val updateApps = mutableListOf<AppInfoModel11>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_device_info)
        imgBack = findViewById(R.id.imgBack)
        imgBack.setOnClickListener { onBackPressed() }


        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AppListAdapter11(updateApps)
        recyclerView.adapter = adapter

        loadAppsWithUpdate()
    }

    private fun loadAppsWithUpdate() {
        val pm = packageManager
        val allApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        lifecycleScope.launch(Dispatchers.IO) {
            for (app in allApps) {
                try {
                    val installer = pm.getInstallerPackageName(app.packageName)
                    if (installer != "com.android.vending") continue

                    val packageInfo = pm.getPackageInfo(app.packageName, 0)
                    val installedVersion = packageInfo.versionName ?: continue
                    val playStoreVersion = fetchLatestVersionFromPlayStore(app.packageName)

                    if (playStoreVersion != null && installedVersion != playStoreVersion) {
                        val model = AppInfoModel11(
                            appName = pm.getApplicationLabel(app).toString(),
                            packageName = app.packageName,
                            versionInstalled = installedVersion,
                            latestVersion = playStoreVersion,
                            icon = pm.getApplicationIcon(app.packageName)
                        )

                        withContext(Dispatchers.Main) {
                            updateApps.add(model)
                            adapter.notifyItemInserted(updateApps.size - 1)
                        }
                    }

                } catch (_: Exception) { }
            }
        }
    }
    private fun fetchLatestVersionFromPlayStore(packageName: String): String? {
        return try {
            val doc = Jsoup.connect("https://play.google.com/store/apps/details?id=$packageName&hl=en&gl=us")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .referrer("http://www.google.com")
                .timeout(10000)
                .get()

            val elements = doc.select("script")

            for (element in elements) {
                val data = element.data()
                if (data.contains("softwareVersion")) {
                    val regex = Regex("(?<=\\\"softwareVersion\\\":\\[\\\")(.*?)(?=\\\")")
                    val match = regex.find(data)
                    return match?.value?.trim()
                }
            }

            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

//    private fun fetchLatestVersionFromPlayStore(packageName: String): String? {
//        return try {
//            val doc = Jsoup.connect("https://play.google.com/store/apps/details?id=$packageName&hl=en")
//                .timeout(10000)
//                .userAgent("Mozilla/5.0")
//                .get()
//
//            val infoElements = doc.select("div.hAyfc")
//            for (element in infoElements) {
//                if (element.select("div.BgcNfc").text() == "Current Version") {
//                    return element.select("span.htlgb").first()?.text()?.trim()
//                }
//            }
//            null
//        } catch (e: Exception) {
//            null
//        }
//    }
}