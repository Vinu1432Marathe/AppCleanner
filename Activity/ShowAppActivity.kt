package com.software.app.update.smart.Activity

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.addsdemo.mysdk.ADPrefrences.MyApp
import com.addsdemo.mysdk.ADPrefrences.NativeAds_Class
import com.software.app.update.smart.R
import java.io.ByteArrayInputStream

class ShowAppActivity : BaseActivity() {

    lateinit var imgBack: ImageView
    lateinit var imgApp: ImageView
    lateinit var txtAppName: TextView
    lateinit var installDateText: TextView
    lateinit var versionText: TextView
    lateinit var sizeText: TextView

    lateinit var container: LinearLayout
    lateinit var txtCheckUpdate: TextView
    lateinit var txtStartApp: TextView
    lateinit var txtUninstall: TextView
    lateinit var txtPlayStore: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_app)

        imgBack = findViewById(R.id.imgBack)
        imgApp = findViewById(R.id.imgApp)
        txtAppName = findViewById(R.id.txtAppName)
        installDateText = findViewById(R.id.installDateText)
        versionText = findViewById(R.id.versionText)
        sizeText = findViewById(R.id.sizeText)

        txtCheckUpdate = findViewById(R.id.txtCheckUpdate)
        txtStartApp = findViewById(R.id.txtStartApp)
        txtUninstall = findViewById(R.id.txtUninstall)
        txtPlayStore = findViewById(R.id.txtPlayStore)

        imgBack.setOnClickListener { onBackPressed() }


        container = findViewById(R.id.container)
        container.isVisible =
            MyApp.ad_preferences.getRemoteConfig()?.isAdShow == true

        val llline_full = findViewById<LinearLayout>(com.addsdemo.mysdk.R.id.llline_full)
        val llnative_full = findViewById<LinearLayout>(com.addsdemo.mysdk.R.id.llnative_full)
        NativeAds_Class.Fix_NativeFull_Show(this, llnative_full, llline_full, "medium")


        val AppName = intent.getStringExtra("AppName")
        val AppVersion = intent.getStringExtra("AppVersion")
        val AppDate = intent.getStringExtra("AppDate")
        val AppSize = intent.getStringExtra("AppSize")
        val AppPackage = intent.getStringExtra("AppPackage")
        val AppIcon = intent.getStringExtra("AppIcon")

        val drawable: Drawable = base64ToDrawable(this, AppIcon.toString())
        imgApp.setImageDrawable(drawable)

        txtAppName.text = AppName
        installDateText.text = AppDate
        versionText.text = AppVersion
        sizeText.text = AppSize


        txtCheckUpdate.setOnClickListener {

            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=${AppPackage}")
                setPackage("com.android.vending")
            }
            startActivity(intent)
        }

        txtStartApp.setOnClickListener {

            val intent = packageManager.getLaunchIntentForPackage(AppPackage.toString())

            if (intent != null) {
                startActivity(intent)
            } else {
                val playIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$AppPackage"))
                startActivity(playIntent)
            }


//            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
//            if (launchIntent != null) {
//                startActivity(launchIntent)
//            } else {
//                val intent = Intent(Intent.ACTION_VIEW)
//                intent.data = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
//                startActivity(intent)
//            }

        }

        txtUninstall.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=${AppPackage}")
                setPackage("com.android.vending")
            }
            startActivity(intent)
        }

        txtPlayStore.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=${AppPackage}")
                setPackage("com.android.vending")
            }
            startActivity(intent)

        }


    }

    fun base64ToDrawable(context: Context, base64Str: String): Drawable {
        val byteArray = Base64.decode(base64Str, Base64.DEFAULT)
        val inputStream = ByteArrayInputStream(byteArray)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        return BitmapDrawable(context.resources, bitmap)
    }
}