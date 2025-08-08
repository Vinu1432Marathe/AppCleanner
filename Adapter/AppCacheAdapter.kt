package com.software.app.update.smart.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.software.app.update.smart.Model.AppCacheModel
import com.software.app.update.smart.R

class AppCacheAdapter : RecyclerView.Adapter<AppCacheAdapter.CacheViewHolder>() {

    private var appList = listOf<AppCacheModel>()

    fun submitList(list: List<AppCacheModel>) {
        appList = list
        notifyDataSetChanged()
    }

    inner class CacheViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.imgAppIcon)
        val name: TextView = view.findViewById(R.id.txtAppName)
        val cache: TextView = view.findViewById(R.id.txtChaches)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CacheViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_app_cache, parent, false)
        return CacheViewHolder(view)
    }

    override fun getItemCount(): Int = appList.size

    override fun onBindViewHolder(holder: CacheViewHolder, position: Int) {
        val app = appList[position]
        holder.icon.setImageDrawable(app.icon)
        holder.name.text = app.appName
        holder.cache.text = formatSize(app.cacheSize)
    }

    private fun formatSize(bytes: Long): String {
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        return if (mb >= 1) String.format("%.2f MB", mb) else String.format("%.2f KB", kb)
    }
}