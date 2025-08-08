package com.software.app.update.smart.Activity

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.StatFs
import android.provider.Settings
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.addsdemo.mysdk.ADPrefrences.MyApp
import com.addsdemo.mysdk.ADPrefrences.NativeAds_Class
import com.software.app.update.smart.R

class DeviceInformationActivity : BaseActivity() {

    lateinit var container : LinearLayout
    lateinit var imgBack : ImageView
    lateinit var txtDevice : TextView
    lateinit var txtModel : TextView
    lateinit var txtRAM : TextView
    lateinit var txtStorage : TextView
    lateinit var txtResolution : TextView
    lateinit var txtDeviceID : TextView
    lateinit var txtAndroid : TextView
    lateinit var txtAndroidSecurity : TextView
    lateinit var txtMIUIVersion : TextView
    lateinit var txtCheckUpdates : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_device_information)

        imgBack = findViewById(R.id.imgBack)
        txtDevice = findViewById(R.id.txtDevice)
        txtModel = findViewById(R.id.txtModel)
        txtRAM = findViewById(R.id.txtRAM)
        txtStorage = findViewById(R.id.txtStorage)
        txtResolution = findViewById(R.id.txtResolution)
        txtDeviceID = findViewById(R.id.txtDeviceID)
        txtAndroid = findViewById(R.id.txtAndroid)
        txtAndroidSecurity = findViewById(R.id.txtAndroidSecurity)
        txtMIUIVersion = findViewById(R.id.txtMIUIVersion)
        txtCheckUpdates = findViewById(R.id.txtCheckUpdates)

        imgBack.setOnClickListener { onBackPressed() }




        txtDevice.text = buildString {
            append(Build.BRAND)
            append(" ")
            append(Build.MANUFACTURER)
        }

        txtModel.text = "Model "+Build.MODEL

        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)


        txtRAM.text = buildString {
        append(memInfo.totalMem / 1024 / 1024)
        append(" MB")}

        val statFs = StatFs(filesDir.absolutePath)
        val blockSize = statFs.blockSizeLong
        val totalBlocks = statFs.blockCountLong
        txtStorage.text = buildString {
        append((blockSize * totalBlocks) / 1024 / 1024)
        append(" MB") }

        val metrics = resources.displayMetrics
        txtResolution.text = buildString {
        append(metrics.widthPixels)
        append(" x ")
        append(metrics.heightPixels) }

        txtDeviceID.text = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID)

//        val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        txtAndroid.text = buildString {
            append(Build.VERSION.RELEASE)
            append("  ")
            append(Build.DISPLAY)
        }

        txtAndroidSecurity.text =buildString {
            append(Build.VERSION.SECURITY_PATCH)
            append(" ")
            append(Build.VERSION.RELEASE)
            append(" ")
            append(Build.VERSION.SDK_INT)
        }

        txtMIUIVersion.text = Build.HARDWARE

        txtCheckUpdates.setOnClickListener {  }


        container = findViewById(R.id.container)
        container.isVisible =
            MyApp.ad_preferences.getRemoteConfig()?.isAdShow == true

        val llline_full = findViewById<LinearLayout>(com.addsdemo.mysdk.R.id.llline_full)
        val llnative_full = findViewById<LinearLayout>(com.addsdemo.mysdk.R.id.llnative_full)
        NativeAds_Class.Fix_NativeFull_Show(this, llnative_full, llline_full, "medium")


//        val infoTextView = findViewById<TextView>(R.id.deviceInfoText)
//        infoTextView.text = getDeviceFullInfo(this)

    }


}