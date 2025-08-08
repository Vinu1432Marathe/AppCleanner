package com.software.app.update.smart.Activity.ScanUpdate

import android.content.pm.ApplicationInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.addsdemo.mysdk.ADPrefrences.MyApp
import com.addsdemo.mysdk.ADPrefrences.NativeAds_Class
import com.software.app.update.smart.Activity.BaseActivity
import com.software.app.update.smart.Adapter.UpdateAppsAdapter
import com.software.app.update.smart.Model.UpdateApps
import com.software.app.update.smart.R
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UpdateAppsActivity : BaseActivity() {

    private lateinit var container: LinearLayout
    private lateinit var rlNodata: RelativeLayout
    private lateinit var imgBack: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UpdateAppsAdapter
    private val appList = mutableListOf<UpdateApps>()

//    private val updateList = mutableListOf<AppInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_update_apps)


        imgBack = findViewById(R.id.imgBack)
        rlNodata = findViewById(R.id.rlNodata)
        recyclerView = findViewById(R.id.recyclerView)
        loadAndCompareVersions()


        container = findViewById(R.id.container)
        container.isVisible =
            MyApp.ad_preferences.getRemoteConfig()?.isAdShow == true

        val llline_full = findViewById<LinearLayout>(com.addsdemo.mysdk.R.id.llline_full)
        val llnative_full = findViewById<LinearLayout>(com.addsdemo.mysdk.R.id.llnative_full)
        NativeAds_Class.Fix_NativeFull_Show(this, llnative_full, llline_full, "medium")


        if (appList.isEmpty()){
            rlNodata.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            container.visibility = View.GONE
        }else{
            rlNodata.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            container.visibility = View.VISIBLE
            adapter = UpdateAppsAdapter(this,appList)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }


        imgBack.setOnClickListener { onBackPressed() }


//        loadUserInstalledApps()

    }

    private fun loadAndCompareVersions() {
        val latestVersions = readVersionMapFromAssets()
        val pm = packageManager
        val installed = pm.getInstalledPackages(0)
        val formatter = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())

        appList.clear()

        for (pkg in installed) {
            val isSystem = (pkg.applicationInfo?.flags?.and(ApplicationInfo.FLAG_SYSTEM)) != 0
            if (!isSystem) {
                val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                    pkg.longVersionCode else pkg.versionCode.toLong()

                val latest = latestVersions[pkg.packageName]
                if (latest != null && versionCode < latest) {
                    val installDate = try {
                        val firstInstallTime = pm.getPackageInfo(pkg.packageName, 0).firstInstallTime
                        formatter.format(Date(firstInstallTime))
                    } catch (e: Exception) {
                        "Unknown"
                    }

                    val sizeInMb = try {
                        val file = File(pkg.applicationInfo!!.sourceDir)
                        val sizeMb = file.length() / (1024 * 1024)
                        "$sizeMb MB"
                    } catch (e: Exception) {
                        "Unknown"
                    }

                    appList.add(
                        UpdateApps(
                            appName = pkg.applicationInfo!!.loadLabel(pm).toString(),
                            packageName = pkg.packageName,
                            versionName = pkg.versionName ?: "N/A",
                            versionCode = versionCode,
                            icon = pkg.applicationInfo!!.loadIcon(pm),
                            installDate = installDate,
                            size = sizeInMb
                        )
                    )
                }
            }
        }
    }

//    private fun loadAndCompareVersions() {
//        val latestVersions = readVersionMapFromAssets()
//        val pm = packageManager
//        val installed = pm.getInstalledPackages(0)
//
//        appList.clear()
//
//        for (pkg in installed) {
//            val isSystem = (pkg.applicationInfo?.flags?.and(ApplicationInfo.FLAG_SYSTEM)) != 0
//            if (!isSystem) {
//                val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
//                    pkg.longVersionCode else pkg.versionCode.toLong()
//
//                val latest = latestVersions[pkg.packageName]
//                if (latest != null && versionCode < latest) {
//                    appList.add(
//                        UpdateApps(
//                            appName = pkg.applicationInfo!!.loadLabel(pm).toString(),
//                            packageName = pkg.packageName,
//                            versionName = pkg.versionName ?: "N/A",
//                            versionCode = versionCode,
//                            icon = pkg.applicationInfo!!.loadIcon(pm)
//                        )
//                    )
//                }
//            }
//        }
//
//
//    }

    private fun readVersionMapFromAssets(): Map<String, Long> {
        val json = assets.open("app_data.json").bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(json)
        val map = mutableMapOf<String, Long>()
        jsonObject.keys().forEach { key ->
            map[key] = jsonObject.getLong(key)
        }
        return map
    }


}