package com.example.myapplication

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FileAdapter(
    private var fileList: List<FileItem>,
    private val onItemClick: (FileItem) -> Unit,
    private val onItemLongClick: (FileItem, View) -> Boolean
) : RecyclerView.Adapter<FileAdapter.FileViewHolder>() {

    class FileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iconImageView: ImageView = view.findViewById(R.id.iconImageView)
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        val detailsTextView: TextView = view.findViewById(R.id.detailsTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_file, parent, false)
        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val fileItem = fileList[position]

        holder.nameTextView.text = fileItem.name

        if (fileItem.isDirectory) {
            holder.iconImageView.setImageResource(android.R.drawable.ic_menu_view)
            holder.iconImageView.setColorFilter(Color.rgb(255, 193, 7))
            holder.detailsTextView.text = "Thư mục"
        } else {
            when {
                fileItem.isImageFile() -> {
                    holder.iconImageView.setImageResource(android.R.drawable.ic_menu_gallery)
                    holder.iconImageView.setColorFilter(Color.rgb(76, 175, 80))
                }
                fileItem.isTextFile() -> {
                    holder.iconImageView.setImageResource(android.R.drawable.ic_menu_edit)
                    holder.iconImageView.setColorFilter(Color.rgb(33, 150, 243))
                }
                else -> {
                    holder.iconImageView.setImageResource(android.R.drawable.ic_menu_info_details)
                    holder.iconImageView.setColorFilter(Color.GRAY)
                }
            }
            holder.detailsTextView.text = fileItem.getFormattedSize()
        }

        holder.itemView.setOnClickListener {
            onItemClick(fileItem)
        }

        holder.itemView.setOnLongClickListener {
            onItemLongClick(fileItem, it)
        }
    }

    override fun getItemCount(): Int = fileList.size

    fun updateList(newList: List<FileItem>) {
        fileList = newList
        notifyDataSetChanged()
    }
}