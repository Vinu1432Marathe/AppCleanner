package com.software.app.update.smart.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.software.app.update.smart.Model.AppUsageModel
import com.software.app.update.smart.R
import java.util.Date
import java.util.concurrent.TimeUnit

class AppUsageAdapter(private val apps: List<AppUsageModel>) :
    RecyclerView.Adapter<AppUsageAdapter.UsageViewHolder>() {

    class UsageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.appIcon)
        val name: TextView = itemView.findViewById(R.id.appName)
        val time: TextView = itemView.findViewById(R.id.timeSpent)
        val count: TextView = itemView.findViewById(R.id.launchCount)
        val lastUsed: TextView = itemView.findViewById(R.id.lastUsed)
        val dataUsed: TextView = itemView.findViewById(R.id.dataUsed)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_app_usage, parent, false)
        return UsageViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsageViewHolder, position: Int) {
        val app = apps[position]
        holder.icon.setImageDrawable(app.icon)
        holder.name.text = app.appName
        holder.time.text = "Screen Time: ${formatDuration(app.timeSpent)}"
        holder.count.text = "Opens: ${app.launchCount}"
        holder.lastUsed.text = "Last Used: ${Date(app.lastUsed)}"
        holder.dataUsed.text = "Data: ${formatData(app.dataUsedBytes)}"
    }

    override fun getItemCount(): Int = apps.size

    private fun formatDuration(ms: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(ms) % 60
        val hours = TimeUnit.MILLISECONDS.toHours(ms)
        return "${hours}h ${minutes}m"
    }

    private fun formatData(bytes: Long): String {
        val mb = bytes / (1024.0 * 1024.0)
        return "%.2f MB".format(mb)
    }
}