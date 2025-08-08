package com.software.app.update.smart.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.software.app.update.smart.R

class ShowAdroDataAdapter(var activity: Activity, private val items: List<String>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

//    private val mergedList: ArrayList<Any> = ArrayList()
//    private val TYPE_NORMAL = 0
//    private val TYPE_AD = 1
//    private val SHOW_AD = "SHOW_AD"
//    private var nextAdPosition = 3 // First ad after 3 items
//
//    init {
//        mergeDataWithAds(items)
//    }
//
//    private fun mergeDataWithAds(items: List<String>) {
//        mergedList.clear()
//        var count = 0
//        for (item in items) {
//            mergedList.add(item)
//            count++
//            if (count == nextAdPosition) {
//                mergedList.add(SHOW_AD)
//                nextAdPosition += 9 // Next ad after every 5 items
//            }
//        }
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        return if (mergedList[position] is String && mergedList[position] == SHOW_AD) {
//            TYPE_AD
//        } else {
//            TYPE_NORMAL
//        }
//    }

    inner class DataViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val captionText = view.findViewById<TextView>(R.id.captionText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        return if (viewType == TYPE_AD) {
//            val view =
//                LayoutInflater.from(parent.context).inflate(R.layout.ad_layout, parent, false)
//            AdViewHolder(view)
//        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_show_adro, parent, false)
            return DataViewHolder(view)
//        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

//        if (getItemViewType(position) == TYPE_AD) {
//            if (UtilsClass.remoteConfigModel != null) {
//                val adHolder = holder as AdViewHolder
//                adHolder.llline_full.layoutParams.height = 300
//                Ads_Listing_Adapter.NativeFull_Show(
//                    activity,
//                    adHolder.llnative_full,
//                    adHolder.llline_full,
//                    "small",
//                    position
//                )
//            }
//        } else {
            val captionHolder = holder as DataViewHolder
            val item = items[position] as String
            captionHolder.captionText.text = item

//            captionHolder.itemView.setOnClickListener {
//                val intent =
//                    Intent(activity, CopyDataActivity::class.java).putExtra("CopyData", item)
//                UtilsClass.startSpecialActivity(activity, intent, false)
//            }
//        }

//        holder.captionText.text = items[position]
//
//        holder.itemView.setOnClickListener {
//
//            val intent = Intent(activity, CopyDataActivity::class.java).putExtra("CopyData",items[position])
//            UtilsClass.startSpecialActivity(activity, intent, false);
//
//        }
    }

//    inner class AdViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val llline_full: LinearLayout = view.findViewById(R.id.llline_full)
//        val llnative_full: LinearLayout = view.findViewById(R.id.llnative_full)
//    }

    override fun getItemCount(): Int = items.size
}