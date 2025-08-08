package com.software.app.update.smart.Language

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.addsdemo.mysdk.ADPrefrences.NativeAds_Class
import com.software.app.update.smart.Activity.AppShowActivity
import com.software.app.update.smart.Activity.BaseActivity
import com.software.app.update.smart.Activity.MainActivity
import com.software.app.update.smart.Other.AppUtils.languages_list
import com.software.app.update.smart.Other.PreferencesHelper
import com.software.app.update.smart.Other.PreferencesHelper11
import com.software.app.update.smart.Other.SharePref
import com.software.app.update.smart.R
import java.util.Locale

class LanguageActivity : BaseActivity(), AdapterLanguage.OnLanguageSelectedListener,
    OnSharedPreferenceChangeListener {


    lateinit var rclLanguage: RecyclerView
    lateinit var txtDone: TextView
    lateinit var txtHeader: TextView
    lateinit var imgBack: ImageView

    private lateinit var preferencesHelper: PreferencesHelper
    lateinit var selectedLanguage: String

    override fun attachBaseContext(newBase: Context) {
        selectedLanguage = PreferencesHelper11(newBase).selectedLanguage ?: "en"
        super.attachBaseContext(setLocale(newBase, selectedLanguage))
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_language)

        rclLanguage = findViewById(R.id.rclLanguage)
        txtDone = findViewById(R.id.txtDone)
        txtHeader = findViewById(R.id.txtHeader)
        imgBack = findViewById(R.id.imgBack)


//        // todo Ads code ....
        val llline_full = findViewById<LinearLayout>(com.addsdemo.mysdk.R.id.llline_full)
        val llnative_full = findViewById<LinearLayout>(com.addsdemo.mysdk.R.id.llnative_full)
        NativeAds_Class.NativeFull_Show(this, llnative_full, llline_full, "large")

        if (SharePref.isOnboarding(this)) {
            imgBack.visibility = View.VISIBLE
        } else {
            imgBack.visibility = View.GONE
        }

        imgBack.setOnClickListener {
            onBackPressed()
        }

        Log.e("CheckLangua", "Language  : $selectedLanguage")

        val adapter = AdapterLanguage(this, languages_list, this, 0)
        rclLanguage.adapter = adapter
        rclLanguage.layoutManager = LinearLayoutManager(this)

        txtDone.setOnClickListener {


            PreferencesHelper11(this).apply {
                selectedLanguage = this@LanguageActivity.selectedLanguage
                isLangSetOnce = true
            }
            selectedLanguage.let {
//                PreferencesHelper(this).isLangSetOnce = true
//                setLocale(it)
//                preferencesHelper.selectedLanguage = it


                if (SharePref.isOnboarding(this)) {
//                    startSpecialActivity(this, Intent(this, MainActivity::class.java), false)


                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
//                    mainActivity?.recreate()
                    finish()
                } else {
                    val intent = Intent(this, AppShowActivity::class.java)
                    startActivity(intent)

//                    startSpecialActivity(this, Intent(this, AppSlideActivity::class.java), false)
                }
            }
        }

    }

    override fun onBackPressed() {
        if (SharePref.isOnboarding(this)) {

            finish()
        } else {

            finishAffinity()
        }
    }

    fun setLocale(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }


    override fun onLanguageSelected(language: Model_Language) {
        selectedLanguage = language.lag_code
    }

    override fun onSharedPreferenceChanged(
        p0: SharedPreferences?,
        p1: String?
    ) {
        TODO("Not yet implemented")
    }
}