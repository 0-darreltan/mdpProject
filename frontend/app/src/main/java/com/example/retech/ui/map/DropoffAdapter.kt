package com.example.retech.ui.map

import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.retech.databaseModel.Locations
import com.example.retech.databinding.ItemDropoffBinding
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

class DropoffAdapter(private var listDropoff: List<Locations>) :
    RecyclerView.Adapter<DropoffAdapter.ViewHolder>() {

    private var userLatLng: LatLng? = null

    class ViewHolder(val binding: ItemDropoffBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDropoffBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    fun updateList(newList: List<Locations>) {
        listDropoff = newList
        notifyDataSetChanged()
    }

    fun updateData(newList: List<Locations>, location: LatLng?) {
        this.listDropoff = newList
        this.userLatLng = location
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listDropoff[position]

        holder.binding.tvItemName.text = data.name
        holder.binding.tvItemAddress.text = data.address

        // Hitung jarak real-time jika lokasi user tersedia
        if (userLatLng != null) {
            val results = FloatArray(1)
            Location.distanceBetween(
                userLatLng!!.latitude, userLatLng!!.longitude,
                data.latitude, data.longitude,
                results
            )
            val distanceInKm = results[0] / 1000
            holder.binding.tvItemDistance.text = String.format(Locale.getDefault(), "%.1f km", distanceInKm)
            holder.binding.tvItemDistance.visibility = View.VISIBLE
        } else {
            holder.binding.tvItemDistance.text = "- km"
        }

        Glide.with(holder.itemView.context)
            .load(data.image_url)
            .centerCrop()
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_report_image)
            .into(holder.binding.ivItemIcon)

        holder.binding.tvItemAccept1.visibility = View.GONE
        holder.binding.tvItemAccept2.visibility = View.GONE

        if (data.accepted_items.isNotEmpty()) {
            holder.binding.tvItemAccept1.text = data.accepted_items[0]
            holder.binding.tvItemAccept1.visibility = View.VISIBLE
        }
        if (data.accepted_items.size > 1) {
            holder.binding.tvItemAccept2.text = data.accepted_items[1]
            holder.binding.tvItemAccept2.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int = listDropoff.size
}