package com.software.app.update.smart.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.addsdemo.mysdk.utils.UtilsClass
import com.google.gson.Gson
import com.software.app.update.smart.Adapter.AndroVersionAdapter
import com.software.app.update.smart.Model.AndroVersion
import com.software.app.update.smart.Other.AppUtils.lstAndroVersion
import com.software.app.update.smart.Other.LocaleHelper
import com.software.app.update.smart.Other.PreferencesHelper11
import com.software.app.update.smart.R
import kotlin.jvm.java

class AndroidVersionActivity : BaseActivity() {

    lateinit var adapter: AndroVersionAdapter
    lateinit var recyclerView: RecyclerView
    lateinit var imgBack: ImageView

    lateinit var progressBar: ProgressBar

    override fun attachBaseContext(newBase: Context) {
        val langCode = PreferencesHelper11(newBase).selectedLanguage ?: "en"
        super.attachBaseContext(LocaleHelper.setLocale(newBase, langCode))
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_android_version)


        imgBack = findViewById(R.id.imgBack)
        imgBack.setOnClickListener { onBackPressed() }
        recyclerView = findViewById(R.id.recyclerView)

        progressBar = findViewById(R.id.progressBar)


        // Show progress bar
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        val json = loadJSONFromAssets(this, "app_version.json")
        Log.e("CheckList", "List :: $json")
        json?.let {
            val appVersion = Gson().fromJson(it, AndroVersion::class.java)

            Log.e("CheckList", "Android Version   $appVersion")

            // Simulate short delay to show loading effect (optional)
            Handler(Looper.getMainLooper()).postDelayed({

                adapter = AndroVersionAdapter(
                    this,
                    appVersion.Version,
                    lstAndroVersion
                ) { selectedCategory,pos ->
                    val intent = Intent(this, ShowAndroDataActivity::class.java).apply {
                        putExtra("AndroVersion", selectedCategory.AndroVersion)
                        putExtra("VerName", selectedCategory.VerName)
                        putExtra("ReleaseDate", selectedCategory.ReleaseDate)
                        putExtra("position", pos)
                        putStringArrayListExtra("captionData", ArrayList(selectedCategory.data))
                    }
//                    startActivity(intent)
                    UtilsClass.startSpecialActivity(this, intent, false);
                }
                val layoutManager = GridLayoutManager(this, 3)
                layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if ((adapter.getItemViewType(position) == adapter.TYPE_NORMAL)) 1 else 3
                    }
                }
                recyclerView.layoutManager = layoutManager
                recyclerView.adapter = adapter
                recyclerView.setHasFixedSize(true)


                // Hide progress bar after loading
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }, 1000) // Optional delay
        } ?: run {
            Toast.makeText(this, getString(R.string.failed_to_load_data), Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.GONE
        }


    }

    private fun loadJSONFromAssets(context: Context, fileName: String): String? {
        return try {
            val input = context.assets.open(fileName)
            val size = input.available()
            val buffer = ByteArray(size)
            input.read(buffer)
            input.close()
            String(buffer, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("CheckList", "MM : ${e.printStackTrace()}")
            null
        }
    }
}