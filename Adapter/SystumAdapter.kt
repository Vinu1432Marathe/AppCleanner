package com.software.app.update.smart.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.addsdemo.mysdk.ADPrefrences.Ads_Listing_Adapter
import com.addsdemo.mysdk.ADPrefrences.Premium_Advertise_AdsReward.activity
import com.addsdemo.mysdk.utils.UtilsClass
import com.software.app.update.smart.Activity.ShowAppActivity
import com.software.app.update.smart.Model.AppInfo1
import com.software.app.update.smart.R
import java.io.ByteArrayOutputStream

class SystumAdapter(
    private val activity: Activity,                // ✅ Use Activity directly
    private val apps: List<AppInfo1>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mergedList: ArrayList<Any> = ArrayList()
    private val TYPE_NORMAL = 0
    private val TYPE_AD = 1
    private val SHOW_AD = "SHOW_AD"
    private var nextAdPosition = 2

    init {
        if (!Ads_Listing_Adapter.admob_nativehashmap.isNullOrEmpty()) {
            Ads_Listing_Adapter.admob_nativehashmap.clear()
        }
        mergeDatatoList(apps)
    }

    override fun getItemViewType(position: Int): Int {
        return if (mergedList[position] is String) TYPE_AD else TYPE_NORMAL
    }

    private fun mergeDatatoList(newItems: List<AppInfo1>) {
        for (item in newItems) {
            mergedList.add(item)
            if (mergedList.size == nextAdPosition) {
                mergedList.add(SHOW_AD)
                nextAdPosition += 9
            }
        }
    }

    class AppViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.appIcon)
        val name: TextView = view.findViewById(R.id.appName)
        val version: TextView = view.findViewById(R.id.appVersion)
        val installDate: TextView = view.findViewById(R.id.appInstallDate)
        val size: TextView = view.findViewById(R.id.appSize)
        val updateBtn: TextView = view.findViewById(R.id.updateButton)
    }

    inner class AdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val llline_full: LinearLayout = itemView.findViewById(R.id.llline_full)
        val llnative_full: LinearLayout = itemView.findViewById(R.id.llnative_full)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_AD) {
            val adView = LayoutInflater.from(parent.context).inflate(R.layout.ad_layout, parent, false)
            AdViewHolder(adView)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.adpater_systum, parent, false)
            AppViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_AD) {
            val adHolder = holder as AdViewHolder
            adHolder.llline_full.layoutParams.height = 300

            if (UtilsClass.remoteConfigModel != null) {
                Ads_Listing_Adapter.NativeFull_Show(
                    activity,
                    adHolder.llnative_full,
                    adHolder.llline_full,
                    "small",
                    position
                )
            }
        } else {
            val appHolder = holder as AppViewHolder
            val app = mergedList[position] as AppInfo1

            appHolder.icon.setImageDrawable(app.icon)
            appHolder.name.text = app.appName
            appHolder.version.text = app.version
            appHolder.installDate.text = app.installDate
            appHolder.size.text = app.size

            appHolder.updateBtn.setOnClickListener {
                val base64Icon = drawableToBase64(app.icon)
                val intent = Intent(activity, ShowAppActivity::class.java).apply {
                    putExtra("AppName", app.appName)
                    putExtra("AppVersion", app.version)
                    putExtra("AppDate", app.installDate)
                    putExtra("AppSize", app.size)
                    putExtra("AppPackage", app.packageName)
                    putExtra("AppIcon", base64Icon)
                }
                UtilsClass.startSpecialActivity(activity, intent, false)
            }
        }
    }

    override fun getItemCount(): Int = mergedList.size

    private fun drawableToBase64(drawable: Drawable): String {
        val bitmap = drawableToBitmap(drawable)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        return if (drawable is BitmapDrawable && drawable.bitmap != null) {
            drawable.bitmap
        } else {
            val width = drawable.intrinsicWidth.takeIf { it > 0 } ?: 1
            val height = drawable.intrinsicHeight.takeIf { it > 0 } ?: 1
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }
}
