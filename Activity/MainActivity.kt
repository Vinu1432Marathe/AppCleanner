package com.software.app.update.smart.Activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import com.addsdemo.mysdk.ADPrefrences.MyApp
import com.addsdemo.mysdk.ADPrefrences.NativeAds_Class
import com.addsdemo.mysdk.ADPrefrences.Premium_Advertise_AdsReward.activity
import com.addsdemo.mysdk.utils.UtilsClass
import com.software.app.update.smart.Activity.ScanUpdate.ScanForUpdatesActivity
import com.software.app.update.smart.R

class MainActivity : BaseActivity() {

    lateinit var container : LinearLayout
    lateinit var imgSetting : ImageView
    lateinit var txtScanNow : TextView
    lateinit var cad_InstalledApp : CardView
    lateinit var cad_SystemApps : CardView
    lateinit var cad_BulkUninstaller : CardView
    lateinit var cad_Duplicate : CardView
    lateinit var cad_AndroidVersion : CardView
    lateinit var cad_DaviceInfo : CardView
    lateinit var cad_AppUsage : CardView
    lateinit var cad_BatteryInfo : CardView
    lateinit var cad_JunkCleaner : CardView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        txtScanNow = findViewById(R.id.txtScanNow)
        cad_InstalledApp = findViewById(R.id.cad_InstalledApp)
        cad_SystemApps = findViewById(R.id.cad_SystemApps)
        cad_BulkUninstaller = findViewById(R.id.cad_BulkUninstaller)
        cad_Duplicate = findViewById(R.id.cad_Duplicate)
        cad_AndroidVersion = findViewById(R.id.cad_AndroidVersion)
        cad_DaviceInfo = findViewById(R.id.cad_DaviceInfo)
        cad_AppUsage = findViewById(R.id.cad_AppUsage)
        cad_BatteryInfo = findViewById(R.id.cad_BatteryInfo)
        cad_JunkCleaner = findViewById(R.id.cad_JunkCleaner)
        imgSetting = findViewById(R.id.imgSetting)


        container = findViewById(R.id.container)
        container.isVisible =
            MyApp.ad_preferences.getRemoteConfig()?.isAdShow == true

        val llline_full = findViewById<LinearLayout>(com.addsdemo.mysdk.R.id.llline_full)
        val llnative_full = findViewById<LinearLayout>(com.addsdemo.mysdk.R.id.llnative_full)
        NativeAds_Class.Fix_NativeFull_Show(this, llnative_full, llline_full, "medium")



        imgSetting.setOnClickListener {
            val intent = Intent(this, DeviceInfoActivity::class.java)
            startActivity(intent)

        }

        txtScanNow.setOnClickListener {

            val intent = Intent(this, ScanForUpdatesActivity::class.java)
            UtilsClass.startSpecialActivity(this, intent, false)

        }

        cad_InstalledApp.setOnClickListener {

            val intent = Intent(this, InstalledAppActivity::class.java)
            UtilsClass.startSpecialActivity(this, intent, false)
        }

        cad_SystemApps.setOnClickListener {

            val intent = Intent(this, SystumAppActivity::class.java)
            UtilsClass.startSpecialActivity(this, intent, false)
        }

        cad_BulkUninstaller.setOnClickListener {

            val intent = Intent(this, BulkUninstallerActivity::class.java)
            UtilsClass.startSpecialActivity(this, intent, false)

        }

        cad_Duplicate.setOnClickListener {

            val intent = Intent(this, DublicateActivity::class.java)
            UtilsClass.startSpecialActivity(this, intent, false)


        }

        cad_AndroidVersion.setOnClickListener {

            val intent = Intent(this, AndroidVersionActivity::class.java)
            UtilsClass.startSpecialActivity(this, intent, false)

        }

        cad_DaviceInfo.setOnClickListener {

            val intent = Intent(this, DeviceInformationActivity::class.java)
            UtilsClass.startSpecialActivity(this, intent, false)

        }

        cad_AppUsage.setOnClickListener {

            val intent = Intent(this, AppUsageActivity::class.java)
            UtilsClass.startSpecialActivity(this, intent, false)


        }

        cad_BatteryInfo.setOnClickListener {

            val intent = Intent(this, BatteryInfoActivity::class.java)
            UtilsClass.startSpecialActivity(this, intent, false)

        }

        cad_JunkCleaner.setOnClickListener {

            val intent = Intent(this, JunkCleanerActivity::class.java)
            UtilsClass.startSpecialActivity(this, intent, false)

        }

    }

    override fun onBackPressed() {
        showCongratulationsDialog()
    }



    private fun showCongratulationsDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_exit)
        dialog.setCancelable(false)

        val mbtn_no = dialog.findViewById<TextView>(R.id.btnCancel)
        val mbtn_yes = dialog.findViewById<TextView>(R.id.btnExit)

        mbtn_yes.setOnClickListener { finishAffinity() }

        mbtn_no.setOnClickListener { dialog.dismiss() }
        val window = dialog.window
        val wlp = window!!.attributes

        wlp.gravity = Gravity.CENTER
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_BLUR_BEHIND.inv()
        window.attributes = wlp
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        dialog.show()

    }

}