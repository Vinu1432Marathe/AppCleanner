package com.software.app.update.smart.Activity

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.software.app.update.smart.Adapter.AppListAdapter
import com.software.app.update.smart.Model.AppInfoModel
import com.software.app.update.smart.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InstalledAppActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppListAdapter
    private lateinit var imgBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_installed_app)

        recyclerView = findViewById(R.id.recyclerView)
        imgBack = findViewById(R.id.imgBack)


        imgBack.setOnClickListener { onBackPressed() }
        recyclerView.layoutManager = LinearLayoutManager(this)

        val apps = getPlayStoreInstalledApps()
        adapter = AppListAdapter(this@InstalledAppActivity, apps)
        recyclerView.adapter = adapter

    }

    private fun getPlayStoreInstalledApps(): List<AppInfoModel> {
        val pm = packageManager
        val allApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val formatter = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())

        return allApps.mapNotNull { app ->
            val installer = pm.getInstallerPackageName(app.packageName)
            if (installer != "com.android.vending") return@mapNotNull null

            try {
                val packageInfo = pm.getPackageInfo(app.packageName, 0)
                val version = packageInfo.versionName ?: "1.0"
                val installTime = packageInfo.firstInstallTime
                val date = formatter.format(Date(installTime))

                val file = File(app.sourceDir)
                val sizeInMb = file.length() / (1024 * 1024)

                AppInfoModel(
                    appName = pm.getApplicationLabel(app).toString(),
                    packageName = app.packageName,
                    version = version,
                    installDate = date,
                    size = "$sizeInMb MB",
                    icon = pm.getApplicationIcon(app)
                )
            } catch (e: Exception) {
                null
            }
        }
    }


}