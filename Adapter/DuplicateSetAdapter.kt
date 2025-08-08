package com.software.app.update.smart.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.software.app.update.smart.Model.ImageInfo
import com.software.app.update.smart.R
import java.io.File
import kotlin.collections.flatten

class DuplicateSetAdapter(
    private val duplicateSets: List<List<ImageInfo>>,
    private val onItemSelectionChanged: (selectedImages: List<ImageInfo>) -> Unit
) : RecyclerView.Adapter<DuplicateSetAdapter.ImageViewHolder>() {

    private val selectedImages = mutableSetOf<ImageInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun getItemCount(): Int = duplicateSets.flatten().size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageData = duplicateSets.flatten()[position]

        Glide.with(holder.itemView.context)
            .load(File(imageData.path))
            .into(holder.imageView)

        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = selectedImages.contains(imageData)

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) selectedImages.add(imageData) else selectedImages.remove(imageData)
            onItemSelectionChanged(selectedImages.toList())
        }

        holder.itemView.setOnClickListener {
            holder.checkBox.isChecked = !holder.checkBox.isChecked
        }
    }

    fun isAllSelected(): Boolean = selectedImages.size == duplicateSets.flatten().size


    fun getSelectedImages(): List<ImageInfo> = selectedImages.toList()

    fun selectAll() {
        selectedImages.clear()
        selectedImages.addAll(duplicateSets.flatten())
        notifyDataSetChanged()
    }

    fun clearSelection() {
        selectedImages.clear()
        notifyDataSetChanged()
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imgPhoto)
        val checkBox: CheckBox = itemView.findViewById(R.id.imgCheck)
    }
}

