package com.software.app.update.smart.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.software.app.update.smart.Model.JunkCategory
import com.software.app.update.smart.Model.JunkItem
import com.software.app.update.smart.R

class JunkAdapter(private val categories: List<JunkCategory>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    private val flatList = mutableListOf<Any>()

    init {
        rebuildList()
    }

    private fun rebuildList() {
        flatList.clear()
        for (category in categories) {
            flatList.add(category)
            if (category.isExpanded) {
                flatList.addAll(category.items)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (flatList[position] is JunkCategory) TYPE_HEADER else TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_junk_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_junk_file, parent, false)
            ItemViewHolder(view)
        }
    }

    override fun getItemCount(): Int = flatList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            val category = flatList[position] as JunkCategory
            holder.bind(category)
        } else if (holder is ItemViewHolder) {
            val item = flatList[position] as JunkItem
            holder.bind(item)
        }
    }

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        private val tvSize: TextView = view.findViewById(R.id.tvSize)

        fun bind(category: JunkCategory) {
            tvCategory.text = category.title
            tvSize.text = String.format("%.1f MB", category.totalSize)
            itemView.setOnClickListener {
                category.isExpanded = !category.isExpanded
                rebuildList()
                notifyDataSetChanged()
            }
        }
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val appIcon: ImageView = view.findViewById(R.id.appIcon)
        private val appName: TextView = view.findViewById(R.id.appName)
        private val appSize: TextView = view.findViewById(R.id.appSize)
        private val checkbox: CheckBox = view.findViewById(R.id.checkbox)

        fun bind(item: JunkItem) {
            appIcon.setImageDrawable(item.appIcon)
            appName.text = item.appName
            appSize.text = String.format("%.1f MB", item.sizeInMB)
            checkbox.isChecked = item.isChecked
        }
    }
}