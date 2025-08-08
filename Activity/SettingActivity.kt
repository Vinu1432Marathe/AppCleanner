package com.software.app.update.smart.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.software.app.update.smart.Language.LanguageActivity
import com.software.app.update.smart.Other.AppUtils
import com.software.app.update.smart.Other.ThemeUtils
import com.software.app.update.smart.R

class SettingActivity : BaseActivity() {

    lateinit var imgBack: ImageView
    lateinit var swThemeMode: Switch

    lateinit var cad_Language: LinearLayout
    lateinit var cad_ShareApp: LinearLayout
    lateinit var cad_RateApp: LinearLayout
    lateinit var cad_PrivacyPolicy: LinearLayout
    lateinit var txtVersion: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        imgBack = findViewById(R.id.imgBack)
        swThemeMode = findViewById(R.id.swThemeMode)
        cad_Language = findViewById(R.id.cad_Language)
        cad_ShareApp = findViewById(R.id.cad_ShareApp)
        cad_RateApp = findViewById(R.id.cad_RateApp)
        cad_PrivacyPolicy = findViewById(R.id.cad_PrivacyPolicy)
        txtVersion = findViewById(R.id.txtVersion)

        imgBack.setOnClickListener { onBackPressed() }

        val context = applicationContext
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)

        val versionName = packageInfo.versionName
//        val versionCode = packageInfo.longVersionCode
        txtVersion.text = versionName

        swThemeMode.isChecked = ThemeUtils.getSavedTheme(this) == ThemeUtils.THEME_DARK

        swThemeMode.setOnCheckedChangeListener { _, isChecked ->
            val selectedTheme = if (isChecked) ThemeUtils.THEME_DARK else ThemeUtils.THEME_LIGHT
            ThemeUtils.saveTheme(this, selectedTheme)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        cad_Language.setOnClickListener {
            val intent = Intent(this, LanguageActivity::class.java)
            startActivity(intent)
        }

        cad_ShareApp.setOnClickListener {

            AppUtils.shareApp(this)
        }

        cad_RateApp.setOnClickListener {
            AppUtils.rateUs(this)
        }

        cad_PrivacyPolicy.setOnClickListener {
            AppUtils.openPrivacy(this)

        }


    }
}