package com.software.app.update.smart.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.software.app.update.smart.Other.CustomCircularProgressView
import com.software.app.update.smart.R

class JunkCleanerActivity : BaseActivity() {

    private lateinit var progressView: CustomCircularProgressView

    val handler = Handler(Looper.getMainLooper())
    var step = 0

    lateinit var imgBack: ImageView
    lateinit var imgUnuseAPK: ImageView
    lateinit var imgSystemCache: ImageView
    lateinit var imgDownloadedFiles: ImageView
    lateinit var imgLargeFiles: ImageView

    lateinit var progressBar1: ProgressBar
    lateinit var progressBar2: ProgressBar
    lateinit var progressBar3: ProgressBar
    lateinit var progressBar4: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_junk_cleaner)

        progressView = findViewById(R.id.progressView)

        imgUnuseAPK = findViewById(R.id.imgUnuseAPK)
        imgSystemCache = findViewById(R.id.imgSystemCache)
        imgDownloadedFiles = findViewById(R.id.imgDownloadedFiles)
        imgLargeFiles = findViewById(R.id.imgLargeFiles)

        progressBar1 = findViewById(R.id.progressBar1)
        progressBar2 = findViewById(R.id.progressBar2)
        progressBar3 = findViewById(R.id.progressBar3)
        progressBar4 = findViewById(R.id.progressBar4)


        imgBack = findViewById(R.id.imgBack)
        imgBack.setOnClickListener { onBackPressed() }
//        progressView.setProgressAnimated(85)



        val runnable = object : Runnable {
            override fun run() {
                when (step) {
                    0 -> {
                        // After 2 seconds - Show txtInstallApp

                        progressBar1.visibility = View.GONE
                        imgUnuseAPK.visibility = View.VISIBLE
                        progressView.setSubText(getString(R.string.scan_apk))
                        progressView.setProgressAnimated(100)
                        handler.postDelayed(this, 2000)
                    }

                    1 -> {
                        // After 4 seconds total - Show txtUpdateApp

                        progressBar2.visibility = View.GONE
                        imgSystemCache.visibility = View.VISIBLE
                        progressView.setSubText(getString(R.string.system_chache))
                        progressView.setProgressAnimated(100)
                        handler.postDelayed(this, 1000)
                    }

                    2 -> {
                        // After 2 seconds - Show txtInstallApp

                        progressBar3.visibility = View.GONE
                        imgDownloadedFiles.visibility = View.VISIBLE
                        progressView.setSubText(getString(R.string.scan_files))
                        progressView.setProgressAnimated(100)
                        handler.postDelayed(this, 2000)
                    }

                    3 -> {
                        // After 4 seconds total - Show txtUpdateApp

                        progressBar4.visibility = View.GONE
                        imgLargeFiles.visibility = View.VISIBLE
                        progressView.setSubText(getString(R.string.large_files))
                        progressView.setProgressAnimated(100)
                        handler.postDelayed(this, 1000)
                    }

                    4 -> {

                        // Done, stop handler
                        val intent = Intent(this@JunkCleanerActivity, JunkAppsActivity::class.java)
                        startActivity(intent)
                    }
                }
                step++
            }
        }

// Start the sequence with 2 seconds delay
        handler.postDelayed(runnable, 2000)


    }


}