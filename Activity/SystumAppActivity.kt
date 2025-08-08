package com.software.app.update.smart.Activity

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.software.app.update.smart.Adapter.SystumAdapter
import com.software.app.update.smart.Model.AppInfo1
import com.software.app.update.smart.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SystumAppActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SystumAdapter
    private lateinit var imgBack: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_systum_app)


        imgBack = findViewById(R.id.imgBack)
        recyclerView = findViewById(R.id.rclSystum)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val apps = getUserSystemApps(this)
        adapter = SystumAdapter(this@SystumAppActivity, apps)
        recyclerView.adapter = adapter

        imgBack.setOnClickListener { onBackPressed() }

    }

    private fun getUserSystemApps(context: Context): List<AppInfo1> {
        val pm = context.packageManager
        val result = mutableListOf<AppInfo1>()
        val packages = pm.getInstalledPackages(PackageManager.GET_META_DATA)

        for (pkg in packages) {
            val appInfo = pkg.applicationInfo
            val isSystemApp = (appInfo?.flags?.and(ApplicationInfo.FLAG_SYSTEM)) != 0
            val hasLauncher = pm.getLaunchIntentForPackage(pkg.packageName) != null

            if (isSystemApp && hasLauncher) {
                val appName = appInfo?.let { pm.getApplicationLabel(it) }.toString()
                val icon = appInfo?.let { pm.getApplicationIcon(it) }
                val versionName = pkg.versionName ?: "N/A"
                val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    pkg.longVersionCode
                } else {
                    pkg.versionCode.toLong()
                }

                val apkFile = File(appInfo?.sourceDir)
                val appSize = formatSize(apkFile.length())
                val installDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    .format(Date(pkg.firstInstallTime))

                icon?.let {
                    result.add(
                        AppInfo1(
                            appName, pkg.packageName, versionName, installDate,
                            appSize.toString(), it
                        )
                        //                         versionCode
                        //                    )
                    )
                }
            }
        }
        return result
    }

    fun formatSize(size: Long): String {
        val kb = size / 1024
        val mb = kb / 1024
        return if (mb > 0) "$mb MB" else "$kb KB"
    }

    fun formatDate(timeMillis: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(timeMillis))
    }

}
