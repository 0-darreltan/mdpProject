package com.example.retech.ui.inventory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.retech.R
import com.example.retech.databaseModel.Device
import com.example.retech.databinding.ItemDeviceBinding

class DeviceAdapter(
    private var deviceList: MutableList<Device> = mutableListOf()
) : RecyclerView.Adapter<DeviceAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemDeviceBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDeviceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = deviceList[position]
        val context = holder.itemView.context

        // Set device name
        holder.binding.tvDeviceName.text = device.name

        // Set category
        holder.binding.tvDeviceCategory.text = device.category

        // Set purchase year
        holder.binding.tvDeviceYear.text = "Purchased ${device.purchaseYear}"

        // Set condition
        holder.binding.tvConditionLabel.text = "Condition: ${device.condition}"

        // Set condition text color based on condition
        when (device.condition) {
            "Good", "Mint" -> {
                holder.binding.tvConditionLabel.setTextColor(
                    ContextCompat.getColor(context, android.R.color.holo_green_dark)
                )
            }
            "Fair" -> {
                holder.binding.tvConditionLabel.setTextColor(
                    ContextCompat.getColor(context, android.R.color.holo_orange_dark)
                )
            }
            else -> {
                holder.binding.tvConditionLabel.setTextColor(
                    ContextCompat.getColor(context, android.R.color.holo_red_dark)
                )
            }
        }

        // Set badge
        holder.binding.tvDeviceBadge.text = device.badge
        when (device.badge) {
            "High Value" -> {
                holder.binding.tvDeviceBadge.setBackgroundResource(R.drawable.bg_badge_high_value)
                holder.binding.tvDeviceBadge.setTextColor(0xFF0F9D58.toInt())
            }
            "Recycle Ready" -> {
                holder.binding.tvDeviceBadge.setBackgroundResource(R.drawable.bg_badge_recycle_ready)
                holder.binding.tvDeviceBadge.setTextColor(0xFFE65100.toInt())
            }
            "Low Waste" -> {
                holder.binding.tvDeviceBadge.setBackgroundResource(R.drawable.bg_badge_low_waste)
                holder.binding.tvDeviceBadge.setTextColor(0xFF1565C0.toInt())
            }
            else -> {
                holder.binding.tvDeviceBadge.setBackgroundResource(R.drawable.bg_badge_category)
                holder.binding.tvDeviceBadge.setTextColor(0xFF0F9D58.toInt())
            }
        }

        // Set device icon based on category
        val iconRes = when (device.category) {
            "Laptop" -> android.R.drawable.ic_menu_manage
            "Smartphone" -> android.R.drawable.ic_menu_call
            "Tablet" -> android.R.drawable.ic_menu_gallery
            "Monitor" -> android.R.drawable.ic_menu_slideshow
            "Peripheral" -> android.R.drawable.ic_menu_preferences
            else -> android.R.drawable.ic_menu_manage
        }
        holder.binding.ivDeviceIcon.setImageResource(iconRes)

        // Set special condition label for specific badges
        when (device.badge) {
            "Recycle Ready" -> {
                holder.binding.tvConditionLabel.text = "Legacy OS Support"
                holder.binding.tvConditionLabel.setTextColor(0xFFE65100.toInt())
            }
        }
    }

    override fun getItemCount(): Int = deviceList.size

    fun updateList(newList: List<Device>) {
        deviceList.clear()
        deviceList.addAll(newList)
        notifyDataSetChanged()
    }

    fun addDevice(device: Device) {
        deviceList.add(0, device)
        notifyItemInserted(0)
    }
}
