package com.software.app.update.smart.Activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.addsdemo.mysdk.utils.UtilsClass
import com.software.app.update.smart.Adapter.SlideViewPagerAdapter
import com.software.app.update.smart.Model.Model_slide
import com.software.app.update.smart.Other.LocaleHelper
import com.software.app.update.smart.Other.PreferencesHelper11
import com.software.app.update.smart.Other.SharePref
import com.software.app.update.smart.R

import kotlin.jvm.java

class AppShowActivity : BaseActivity() {

    lateinit var viewPager: ViewPager2

    lateinit var imgNext: ImageView
    lateinit var indicatorLayout: LinearLayout

    val lstSlide = mutableListOf<Model_slide?>()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            goToMain()
        } else {
            // User denied permission, open settings
            Toast.makeText(this, "Please enable notifications in settings", Toast.LENGTH_LONG).show()
            openAppSettings()
        }
    }


    override fun attachBaseContext(newBase: Context) {
        val langCode = PreferencesHelper11(newBase).selectedLanguage ?: "en"
        super.attachBaseContext(LocaleHelper.setLocale(newBase, langCode))
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_app_show)

        viewPager = findViewById(R.id.viewPager)
//
        imgNext = findViewById(R.id.txtContinue)
        indicatorLayout = findViewById(R.id.indicatorLayout)


        lstSlide.add(
            Model_slide(
                R.drawable.show1,
                getString(R.string.find_update_apps_easily),
                getString(R.string.keep_your_apps_updated_without_hassle_scan_find_and_install_updates_in_just_a_few_taps),
                0
            )
        )
        lstSlide.add(
            Model_slide(
                R.drawable.show2,
                getString(R.string.free_up_storage_easily),
                getString(R.string.easily_uninstall_multiple_apps_at_once_to_free_up_storage_and_keep_your_phone_running_smoothly),
                0
            )
        )
        lstSlide.add(
            Model_slide(
                R.drawable.show3,
                getString(R.string.coolest_way_to_update_app),
                getString(R.string.update_your_apps_effortlessly_with_the_fastest_and_most_stylish_method_stay_up_to_date_with_just_a_tap),
                0
            )
        )



        viewPager.adapter = SlideViewPagerAdapter(lstSlide as List<Model_slide>)

        setupIndicators()
        setCurrentIndicator(0)
//        TabLayoutMediator(tabDots, viewPager) { _, _ -> }.attach()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setCurrentIndicator(position)
            }
        })

        imgNext.setOnClickListener {
            val current = viewPager.currentItem
            if (current < lstSlide.size - 1) {
                viewPager.setCurrentItem(current + 1, true)

            } else {
                // Navigate to next activity
                goToMain()
            }
        }

    }

    private fun setupIndicators() {
        val indicators = arrayOfNulls<ImageView>(lstSlide.size)
        for (i in indicators.indices) {
            indicators[i] = ImageView(this)
            val layoutParams = LinearLayout.LayoutParams(
                if (i == 0) 12.dpToPx() else 24.dpToPx(),
                if (i == 0) 12.dpToPx() else 8.dpToPx()
            )
            layoutParams.setMargins(8, 0, 8, 0)
            indicators[i]!!.layoutParams = layoutParams
            indicators[i]!!.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    if (i == 0) R.drawable.active_dot else R.drawable.inactive_dot
                )
            )
            indicatorLayout.addView(indicators[i])
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val childCount = indicatorLayout.childCount
        for (i in 0 until childCount) {
            val imageView = indicatorLayout.getChildAt(i) as ImageView
            val layoutParams = LinearLayout.LayoutParams(
                if (i == index) 8.dpToPx() else 15.dpToPx(),
                if (i == index) 8.dpToPx() else 6.dpToPx()
            )
            layoutParams.setMargins(5, 0, 5, 0)
            imageView.layoutParams = layoutParams

            imageView.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    if (i == index) R.drawable.active_dot else R.drawable.inactive_dot
                )
            )

            imageView.animate().scaleX(1f).scaleY(1f).setDuration(300).start()
        }
    }

    fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }



    private fun goToMain() {
        SharePref.setOnboarding(this, true)
        val intent = Intent(this, MainActivity::class.java)
        UtilsClass.startSpecialActivity(this, intent, false);

        finish() // Optional: if you want to finish the current activity
    }

    override fun onBackPressed() {
        if (SharePref.isOnboarding(this)) {

            finish()
        } else {

            finishAffinity()
        }
    }


    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    goToMain()
                }

                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Optional: Show custom explanation before requesting again
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }

                else -> {
                    // First time asking permission
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // No need to request permission below Android 13
            goToMain()
        }
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }
}