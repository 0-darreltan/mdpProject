package com.example.retech.ui.map

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.retech.databinding.ItemAcceptedBinding

class AcceptedItemsAdapter(private val items: List<String>) : RecyclerView.Adapter<AcceptedItemsAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemAcceptedBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAcceptedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemName = items[position]
        holder.binding.tvAcceptedName.text = itemName
        
        // Simple icon selection based on text. You can replace android.R.drawable with your own drawables (e.g. R.drawable.ic_battery)
        val iconRes = when {
            itemName.contains("baterai", ignoreCase = true) || itemName.contains("battery", ignoreCase = true) -> android.R.drawable.ic_lock_idle_charging
            itemName.contains("layar", ignoreCase = true) || itemName.contains("screen", ignoreCase = true) || itemName.contains("monitor", ignoreCase = true) -> android.R.drawable.ic_menu_gallery
            itemName.contains("kabel", ignoreCase = true) || itemName.contains("cable", ignoreCase = true) -> android.R.drawable.ic_menu_sort_by_size
            itemName.contains("hp", ignoreCase = true) || itemName.contains("phone", ignoreCase = true) -> android.R.drawable.ic_menu_call
            else -> android.R.drawable.ic_menu_info_details
        }
        
        holder.binding.ivAcceptedIcon.setImageResource(iconRes)
    }

    override fun getItemCount() = items.size
}
