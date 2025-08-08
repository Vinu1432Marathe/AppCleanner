package com.software.app.update.smart.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.software.app.update.smart.Model.AppData
import com.software.app.update.smart.R

class BulkUninstallerAdapter(
    private val apps: MutableList<AppData>,
    private val onSelectionChanged: (selectedApps: List<AppData>) -> Unit
) : RecyclerView.Adapter<BulkUninstallerAdapter.AppViewHolder>() {

    private val selectedApps = mutableSetOf<AppData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_bulk_installer, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bind(apps[position])
    }

    override fun getItemCount(): Int = apps.size

    fun getSelectedApps(): List<AppData> = selectedApps.toList()

    fun toggleSelectAll(): Boolean {
        return if (selectedApps.size == apps.size) {
            clearSelection()
            false
        } else {
            selectedApps.clear()
            selectedApps.addAll(apps)
            notifyDataSetChanged()
            onSelectionChanged(selectedApps.toList())
            true
        }
    }

    fun clearSelection() {
        selectedApps.clear()
        notifyDataSetChanged()
        onSelectionChanged(emptyList())
    }

    fun updateData(newList: List<AppData>) {
        apps.clear()
        apps.addAll(newList)
        selectedApps.retainAll(apps)
        notifyDataSetChanged()
        onSelectionChanged(selectedApps.toList())
    }

    inner class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val appIcon: ImageView = itemView.findViewById(R.id.appIcon)
        private val appName: TextView = itemView.findViewById(R.id.appName)
        private val installDateText: TextView = itemView.findViewById(R.id.installDateText)
        private val versionText: TextView = itemView.findViewById(R.id.versionText)
        private val sizeText: TextView = itemView.findViewById(R.id.sizeText)
        private val checkbox: CheckBox = itemView.findViewById(R.id.checkBox)

        fun bind(app: AppData) {
            appIcon.setImageDrawable(app.icon)
            appName.text = app.name
            installDateText.text = app.installDate
            versionText.text = "v${app.versionName}"
            sizeText.text = String.format("%.2f MB", app.size / (1024.0 * 1024.0))

            checkbox.setOnCheckedChangeListener(null)
            checkbox.isChecked = selectedApps.contains(app)

            checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) selectedApps.add(app) else selectedApps.remove(app)
                onSelectionChanged(selectedApps.toList())
            }

            itemView.setOnClickListener {
                checkbox.isChecked = !checkbox.isChecked
            }
        }
    }
}


