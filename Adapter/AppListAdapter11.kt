package com.software.app.update.smart.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.software.app.update.smart.Model.AppInfoModel
import com.software.app.update.smart.Model.AppInfoModel11
import com.software.app.update.smart.R
import org.jsoup.Jsoup

@RequiresApi(Build.VERSION_CODES.O)
class AppListAdapter11(private val apps: List<AppInfoModel11>) :
    RecyclerView.Adapter<AppListAdapter11.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.appIcon)
        val name: TextView = view.findViewById(R.id.appName)
        val versions: TextView = view.findViewById(R.id.versions)
        val updateBtn: Button = view.findViewById(R.id.updateBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_demo, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = apps.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = apps[position]
        holder.icon.setImageDrawable(app.icon)
        holder.name.text = app.appName
        holder.versions.text = "Installed: ${app.versionInstalled} | Play Store: ${app.latestVersion}"
        holder.updateBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${app.packageName}"))
            holder.itemView.context.startActivity(intent)
        }
    }
}