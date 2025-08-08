package com.software.app.update.smart.Activity.ScanUpdate

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.software.app.update.smart.Activity.BaseActivity
import com.software.app.update.smart.Model.UpdateApp
import com.software.app.update.smart.Other.CircularProgressView
import com.software.app.update.smart.R
import org.json.JSONObject

class ScanForUpdatesActivity : BaseActivity() {


    lateinit var imgBack: ImageView
    lateinit var txtInstallApp: TextView
    lateinit var txtUpdateApp: TextView

    lateinit var progress1: ProgressBar
    lateinit var progress2: ProgressBar

    private lateinit var progressView: CircularProgressView
    private val appList = mutableListOf<UpdateApp>()


    val handler = Handler(Looper.getMainLooper())
    var step = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_for_updates)
        imgBack = findViewById(R.id.imgBack)
        txtInstallApp = findViewById(R.id.txtInstallApp)
        txtUpdateApp = findViewById(R.id.txtUpdateApp)

        progress1 = findViewById(R.id.progress1)
        progress2 = findViewById(R.id.progress2)

        imgBack.setOnClickListener { onBackPressed() }


        progressView = findViewById<CircularProgressView>(R.id.circularProgress)
        progressView.showSubText = false  // Show text


        val stats = getUpdateStats()


        val runnable = object : Runnable {
            override fun run() {
                when (step) {
                    0 -> {
                        // After 2 seconds - Show txtInstallApp
                        val stats = getUpdateStats()
                        txtInstallApp.text = "${stats.total}"
                        progress1.visibility = View.GONE
                        txtInstallApp.visibility = View.VISIBLE
                        progressView.setProgressAnimated(100)
                        handler.postDelayed(this, 2000)
                    }
                    1 -> {
                        // After 4 seconds total - Show txtUpdateApp
                        val stats = getUpdateStats()
                        txtUpdateApp.text = "${stats.updateNeeded}"
                        progress2.visibility = View.GONE
                        txtUpdateApp.visibility = View.VISIBLE
                        progressView.setProgressAnimated(100)
                        handler.postDelayed(this, 1000)
                    }
                    2 -> {
                        // After 5 seconds total - Update progress to 100%
                        progressView.setProgressAnimated(100)
                        // Done, stop handler
                        val intent = Intent(this@ScanForUpdatesActivity, UpdateAppsActivity::class.java)
                        startActivity(intent)
                    }
                }
                step++
            }
        }

// Start the sequence with 2 seconds delay
        handler.postDelayed(runnable, 2000)

        Log.e("checkApps", " \uD83D\uDCF1 Installed Apps: ${stats.total}\n" +
                "            \uD83D\uDD04 Updates Available: ${stats.updateNeeded}\n" +
                "            ✅ Up to Date: ${stats.upToDate}\n" +
                "            ❓ Unknown Status: ${stats.unknown}", )


    }


    private fun getUpdateStats(): UpdateApp {
        val pm = packageManager
        val apps = pm.getInstalledPackages(0)
        val versionMap = loadVersionMap()

        var total = 0
        var updateNeeded = 0
        var upToDate = 0
        var unknown = 0

        for (pkg in apps) {
            val isSystemApp = (pkg.applicationInfo?.flags?.and(ApplicationInfo.FLAG_SYSTEM)) != 0
            if (!isSystemApp) {
                total++
                val packageName = pkg.packageName
                val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                    pkg.longVersionCode else pkg.versionCode.toLong()

                if (versionMap.containsKey(packageName)) {
                    val latest = versionMap[packageName]!!
                    if (versionCode < latest) {
                        updateNeeded++
                    } else {
                        upToDate++
                    }
                } else {
                    unknown++
                }
            }
        }

        return UpdateApp(total, updateNeeded, upToDate, unknown)
    }

    private fun loadVersionMap(): Map<String, Long> {
        return try {
            val json = assets.open("app_data.json").bufferedReader().use { it.readText() }
            val obj = JSONObject(json)
            val map = mutableMapOf<String, Long>()
            obj.keys().forEach { key ->
                map[key] = obj.getLong(key)
            }
            map
        } catch (e: Exception) {
            Log.e("LoadJSON", "Error: ${e.message}")
            emptyMap()
        }
    }


}



