package com.software.app.update.smart.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.software.app.update.smart.R
import java.io.File

class ImageAdapter(private val images: List<String>) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    private val selectedItems = mutableSetOf<Int>()

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img = view.findViewById<ImageView>(R.id.imgPhoto)
        val check = view.findViewById<ImageView>(R.id.imgCheck)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imagePath = images[position]

        Glide.with(holder.itemView.context).load(File(imagePath)).into(holder.img)

        holder.check.visibility = if (selectedItems.contains(position)) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            if (selectedItems.contains(position)) selectedItems.remove(position)
            else selectedItems.add(position)
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = images.size
}