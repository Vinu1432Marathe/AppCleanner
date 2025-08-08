package com.software.app.update.smart.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.addsdemo.mysdk.ADPrefrences.Ads_Interstitial
import com.addsdemo.mysdk.ADPrefrences.MyApp
import com.addsdemo.mysdk.model.RemoteConfigModel
import com.addsdemo.mysdk.utils.CustomTabLinkOpen
import com.addsdemo.mysdk.utils.UtilsClass
import com.software.app.update.smart.Other.LocaleHelper
import com.software.app.update.smart.Other.PreferencesHelper11
import com.software.app.update.smart.Other.ThemeUtils


open class BaseActivity : AppCompatActivity() {

    companion object {

        val remoteConfigModel: RemoteConfigModel? = MyApp.ad_preferences.getRemoteConfig()
    }

    override fun attachBaseContext(newBase: Context) {
        val langCode = PreferencesHelper11(newBase).selectedLanguage ?: "en"
        super.attachBaseContext(LocaleHelper.setLocale(newBase, langCode))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.applyTheme(this)
        super.onCreate(savedInstanceState)


        // Enable edge-to-edge drawing
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // Set status bar background color
        window.statusBarColor = Color.TRANSPARENT
        // Change status bar icon/text color
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
//        insetsController.isAppearanceLightStatusBars = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11+
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.hide(WindowInsets.Type.navigationBars())
            window.insetsController?.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            // For older versions
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }


    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        Ads_Interstitial.BackshowAds_full(this, object : Ads_Interstitial.OnFinishAds {
            override fun onFinishAds(b: Boolean) {
        finish()
                Log.d("TAG", "onFinishAds5424: "+b+" "+remoteConfigModel?.isOnAdRedirect )
                if (b && remoteConfigModel?.isOnAdRedirect == true) {
                    CustomTabLinkOpen.openLink(
                        this@BaseActivity,
                        UtilsClass.getRandomRedirectLink(MyApp.ad_preferences.getRemoteConfig()!!.customLinks!!.interRedirectLink),
                        "inter_click"
                    )
                }
            }
        })
    }


}
