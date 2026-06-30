package com.example.retech.ui.map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.retech.databinding.FragmentDropoffDetailBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class DropoffDetailFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentDropoffDetailBinding? = null
    private val binding get() = _binding!!
    private var locationData: com.example.retech.databaseModel.Locations? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDropoffDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationData = arguments?.getSerializable("locationData") as? com.example.retech.databaseModel.Locations

        locationData?.let { data ->
            binding.tvNamaDropoffDetail.text = data.name
            binding.tvAlamatDropoffDetail.text = data.address
            
            // Scroll ke map saat tombol ditekan
            binding.btnLocationDetail.setOnClickListener {
                binding.svContentDropoffDetail.post {
                    binding.svContentDropoffDetail.smoothScrollTo(0, binding.cardMapDetail.top)
                }
            }
            
            // Menampilkan gambar lokasi (Dropoff)
            Glide.with(this)
                .load(data.image_url)
                .centerCrop()
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(binding.ivLocationImage)
            
            // Set up RecyclerView for accepted items
            if (data.accepted_items.isNotEmpty()) {
                val adapter = AcceptedItemsAdapter(data.accepted_items)
                binding.rvAcceptedDetail.layoutManager = GridLayoutManager(requireContext(), 2)
                binding.rvAcceptedDetail.adapter = adapter
            }

            // Menampilkan list jam operasional menggunakan layout item (mirip desain gambar)
            binding.llHoursContainer.removeAllViews()
            if (data.operational_hours.isNotEmpty()) {
                for (i in data.operational_hours.indices) {
                    val opHour = data.operational_hours[i]
                    val rowView = layoutInflater.inflate(com.example.retech.R.layout.item_operational_hour, binding.llHoursContainer, false)
                    
                    val tvDays = rowView.findViewById<android.widget.TextView>(com.example.retech.R.id.tvDays)
                    val tvTime = rowView.findViewById<android.widget.TextView>(com.example.retech.R.id.tvTime)
                    val divider = rowView.findViewById<android.view.View>(com.example.retech.R.id.divider)
                    
                    tvDays.text = opHour.days
                    tvTime.text = opHour.time
                    
                    // Jika jadwalnya "Closed" atau "Libur", ubah warna teks jam menjadi merah
                    if (opHour.time.contains("Libur", ignoreCase = true) || opHour.time.contains("Closed", ignoreCase = true)) {
                        tvTime.setTextColor(android.graphics.Color.parseColor("#E53935"))
                    } else {
                        tvTime.setTextColor(android.graphics.Color.parseColor("#0F9D58"))
                    }
                    
                    // Sembunyikan garis pembatas di item yang paling bawah
                    if (i == data.operational_hours.size - 1) {
                        divider.visibility = View.GONE
                    }
                    
                    binding.llHoursContainer.addView(rowView)
                }
            }
            
            // Perbarui status Open/Closed Now
            updateOpenStatus(data.operational_hours)
            
            // Inisialisasi Map
            val mapFragment = childFragmentManager.findFragmentById(com.example.retech.R.id.mapDropoffDetail) as? SupportMapFragment
            mapFragment?.getMapAsync(this)
        }
    }
    
    override fun onMapReady(googleMap: GoogleMap) {
        locationData?.let { data ->
            val position = LatLng(data.latitude, data.longitude)
            googleMap.addMarker(MarkerOptions().position(position).title(data.name))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15f))
            googleMap.uiSettings.isZoomControlsEnabled = true
        }
    }
    
    private fun updateOpenStatus(operationalHours: List<com.example.retech.databaseModel.OperationalHour>) {
        val calendar = java.util.Calendar.getInstance()
        val currentDayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
        // Calendar.DAY_OF_WEEK: 1=Minggu, 2=Senin, 3=Selasa, 4=Rabu, 5=Kamis, 6=Jumat, 7=Sabtu
        val dayNames = arrayOf("Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")
        val todayName = dayNames[currentDayOfWeek - 1]
        
        val currentTime = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(calendar.time)
        var isOpen = false
        
        for (opHour in operationalHours) {
            if (isDayInOperational(todayName, opHour.days)) {
                if (opHour.time.contains("Libur", ignoreCase = true) || opHour.time.contains("Closed", ignoreCase = true)) {
                    isOpen = false
                    break
                }
                
                val times = opHour.time.split("-").map { it.trim() }
                if (times.size == 2) {
                    val openTime = times[0]
                    val closeTime = times[1]
                    if (currentTime >= openTime && currentTime <= closeTime) {
                        isOpen = true
                    }
                }
                break
            }
        }
        
        if (isOpen) {
            binding.tvOpenStatus.text = "Open Now"
            binding.tvOpenStatus.setBackgroundResource(com.example.retech.R.drawable.bg_status_open)
            binding.tvOpenStatus.setCompoundDrawablesWithIntrinsicBounds(com.example.retech.R.drawable.ic_check_circle, 0, 0, 0)
        } else {
            binding.tvOpenStatus.text = "Closed Now"
            binding.tvOpenStatus.setBackgroundResource(com.example.retech.R.drawable.bg_status_closed)
            binding.tvOpenStatus.setCompoundDrawablesWithIntrinsicBounds(com.example.retech.R.drawable.ic_close_circle, 0, 0, 0)
        }
    }
    
    private fun isDayInOperational(todayName: String, daysString: String): Boolean {
        val daysList = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")
        val todayIndex = daysList.indexOf(todayName)
        if (todayIndex == -1) return false

        val parts = daysString.split(" dan ", ignoreCase = true).map { it.trim() }
        
        for (part in parts) {
            if (part.contains("-")) {
                val range = part.split("-").map { it.trim() }
                if (range.size == 2) {
                    val startIndex = daysList.indexOf(range[0])
                    val endIndex = daysList.indexOf(range[1])
                    if (startIndex != -1 && endIndex != -1 && todayIndex in startIndex..endIndex) {
                        return true
                    }
                }
            } else {
                if (part.equals(todayName, ignoreCase = true)) {
                    return true
                }
            }
        }
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}