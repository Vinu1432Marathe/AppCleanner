package com.software.app.update.smart.Adapter

import android.R.attr.name
import android.R.attr.version
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
import com.addsdemo.mysdk.ADPrefrences.Premium_Advertise_AdsReward
import com.addsdemo.mysdk.ADPrefrences.Premium_Advertise_AdsReward.activity
import com.addsdemo.mysdk.utils.UtilsClass
import com.software.app.update.smart.Activity.ShowAppActivity
import com.software.app.update.smart.Model.AppInfoModel
import com.software.app.update.smart.R
import java.io.ByteArrayOutputStream

class AppListAdapter(private val activity: Context, private val apps: List<AppInfoModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val mergedList: ArrayList<Any> = ArrayList()
    val TYPE_NORMAL = 0
    val TYPE_AD = 1
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

    private fun mergeDatatoList(newItems: List<AppInfoModel>) {
        for (item in newItems) {
            mergedList.add(item)
            if (mergedList.size == nextAdPosition) {
                mergedList.add(SHOW_AD)
                nextAdPosition += 9
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_AD) {
            val adView =
                LayoutInflater.from(parent.context).inflate(R.layout.ad_layout, parent, false)
            AdViewHolder(adView)
        } else {
            val view =
                LayoutInflater.from(activity).inflate(R.layout.adapter_app_list, parent, false)
            return AppViewHolder(view)
        }
    }

    inner class AdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val llline_full: LinearLayout = itemView.findViewById(R.id.llline_full)
        val llnative_full: LinearLayout = itemView.findViewById(R.id.llnative_full)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (getItemViewType(position) == TYPE_AD) {
            if (UtilsClass.remoteConfigModel != null) {
                val adHolder = holder as AdViewHolder
                adHolder.llline_full.layoutParams.height = 300
                Ads_Listing_Adapter.NativeFull_Show(
                    activity,
                    adHolder.llnative_full,
                    adHolder.llline_full,
                    "small",
                    position
                )
            }
        } else {
//            (holder as AppViewHolder).bind(mergedList[position] as AppInfoModel)

            val captionHolder = holder as AppViewHolder
            val item = mergedList[position] as AppInfoModel
            captionHolder.name.text = item.appName
            captionHolder.version.text = item.version
            captionHolder.installDate.text = item.installDate
            captionHolder.size.text = item.size
            captionHolder.icon.setImageDrawable(item.icon)



            captionHolder.updateButton.setOnClickListener {
                val base64Icon = drawableToBase64(item.icon)

                val intent = Intent(activity, ShowAppActivity::class.java)
                    .putExtra("AppName", item.appName)
                    .putExtra("AppVersion", item.version)
                    .putExtra("AppDate", item.installDate)
                    .putExtra("AppSize", item.size)
                    .putExtra("AppPackage", item.packageName)
                    .putExtra("AppIcon", base64Icon)

                UtilsClass.startSpecialActivity(activity as Activity?, intent, false)
            }

        }


    }

    override fun getItemCount(): Int = apps.size

    class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.appName)
        val version: TextView = itemView.findViewById(R.id.versionText)
        val installDate: TextView = itemView.findViewById(R.id.installDateText)
        val size: TextView = itemView.findViewById(R.id.sizeText)
        val icon: ImageView = itemView.findViewById(R.id.appIcon)
        val updateButton: TextView = itemView.findViewById(R.id.updateButton)


    }
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