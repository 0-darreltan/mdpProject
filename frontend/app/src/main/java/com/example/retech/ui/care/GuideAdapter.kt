package com.example.retech.ui.care

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.retech.databaseModel.Guide
import com.example.retech.databinding.ItemGuideBinding

class GuideAdapter(
    private var guides: List<Guide>,
    private val onItemClick: ((Guide) -> Unit)? = null
) : RecyclerView.Adapter<GuideAdapter.GuideViewHolder>() {

    class GuideViewHolder(val binding: ItemGuideBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuideViewHolder {
        val binding = ItemGuideBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GuideViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GuideViewHolder, position: Int) {
        val guide = guides[position]
        
        with(holder.binding) {
            tvGuideName.text = guide.name
            tvGuideCategory.text = guide.category
            tvGuideSummary.text = guide.summary

            Glide.with(root.context)
                .load(guide.image_url)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(ivGuideImage)

            val openLink: (android.view.View) -> Unit = {
                if (onItemClick != null) {
                    onItemClick.invoke(guide)
                } else {
                    try {
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(guide.file_url))
                        root.context.startActivity(intent)
                    } catch (e: Exception) {
                        android.widget.Toast.makeText(root.context, "Gagal membuka link panduan", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            }

            root.setOnClickListener(openLink)
            btnReadMore.setOnClickListener(openLink)
        }
    }

    override fun getItemCount(): Int = guides.size

    fun updateData(newGuides: List<Guide>) {
        guides = newGuides
        notifyDataSetChanged()
    }
}
