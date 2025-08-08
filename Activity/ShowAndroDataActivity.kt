package com.software.app.update.smart.Activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.addsdemo.mysdk.ADPrefrences.MyApp
import com.addsdemo.mysdk.ADPrefrences.NativeAds_Class
import com.software.app.update.smart.Adapter.ShowAdroDataAdapter
import com.software.app.update.smart.Other.AppUtils.lstAndroVersion
import com.software.app.update.smart.R

class ShowAndroDataActivity : BaseActivity() {

    lateinit var container: LinearLayout
    lateinit var imgBack: ImageView
    lateinit var imgVersion: ImageView

    lateinit var txtAndroName: TextView
    lateinit var txtVerName: TextView
    lateinit var txtRealeaseDate: TextView

    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_show_andro_data)

        imgBack = findViewById(R.id.imgBack)
        recyclerView = findViewById(R.id.recyclerView)

        txtAndroName = findViewById(R.id.txtAndroName)
        txtVerName = findViewById(R.id.txtVerName)
        txtRealeaseDate = findViewById(R.id.txtRealeaseDate)
        imgVersion = findViewById(R.id.imgVersion)

        imgBack.setOnClickListener { onBackPressed() }

        container = findViewById(R.id.container)
        container.isVisible =
            MyApp.ad_preferences.getRemoteConfig()?.isAdShow == true

        val llline_full = findViewById<LinearLayout>(com.addsdemo.mysdk.R.id.llline_full)
        val llnative_full = findViewById<LinearLayout>(com.addsdemo.mysdk.R.id.llnative_full)
        NativeAds_Class.Fix_NativeFull_Show(this, llnative_full, llline_full, "medium")



        val position = intent.getIntExtra("position", 0)
        val AndroVersion = intent.getStringExtra("AndroVersion")
        val VerName = intent.getStringExtra("VerName")
        val ReleaseDate = intent.getStringExtra("ReleaseDate")
        val captionData = intent.getStringArrayListExtra("captionData") ?: arrayListOf()

        txtAndroName.text = AndroVersion
        txtVerName.text = VerName
        txtRealeaseDate.text = ReleaseDate

        imgVersion.setImageResource(lstAndroVersion.get(position))

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ShowAdroDataAdapter(this, captionData)


    }
}