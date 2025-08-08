package com.software.app.update.smart.Adapter

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.software.app.update.smart.Model.UpdateApps
import com.software.app.update.smart.R

class UpdateAppsAdapter(val activity: Activity,private val apps: List<UpdateApps>) :
    RecyclerView.Adapter<UpdateAppsAdapter.AppViewHolder>() {

    class AppViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.appIcon)
        val name: TextView = view.findViewById(R.id.appName)

        val version: TextView = view.findViewById(R.id.version)
        val appInstallDate: TextView = view.findViewById(R.id.appInstallDate)
        val appSize: TextView = view.findViewById(R.id.appSize)
        val updateStatus: TextView = view.findViewById(R.id.updateStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_update_apps, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = apps[position]
        holder.icon.setImageDrawable(app.icon)
        holder.name.text = app.appName
//        holder.pkg.text = app.packageName
        holder.appInstallDate.text = app.installDate
        holder.appSize.text = app.size
        holder.version.text = "v${app.versionName} (${app.versionCode})"

        holder.updateStatus.setOnClickListener {

            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=${app.packageName}")
                setPackage("com.android.vending")
            }
            activity.startActivity(intent)
        }

    }

    override fun getItemCount() = apps.size
}