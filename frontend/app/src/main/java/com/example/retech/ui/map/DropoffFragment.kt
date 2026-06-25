package com.example.retech.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retech.R
import com.example.retech.data.remote.RetrofitClient
import com.example.retech.databaseModel.Locations
import com.example.retech.databinding.FragmentDropoffBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.launch

class DropoffFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentDropoffBinding? = null
    private val binding get() = _binding!!

    private var mInterfaceMap: GoogleMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    
    private var fullLocationList: List<Locations> = listOf()
    private var userLocation: LatLng? = null
    private var adapter: DropoffAdapter? = null
    
    private var currentQuery = ""
    private var currentSortMode = "proximity" // Default: Terdekat

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            moveCameraToUserLocation()
        } else {
            val istts = LatLng(-7.2913, 112.7586)
            mInterfaceMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(istts, 15f))
            Toast.makeText(requireContext(), "Izin ditolak, menggunakan lokasi default", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDropoffBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val mapFragment = childFragmentManager.findFragmentById(R.id.googleMapDropoff) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.rvNearbyDropoff.layoutManager = LinearLayoutManager(requireContext())
        
        setupSearch()

        getDropoffDataFromBackend()
    }

    private fun setupSearch() {
        binding.etNameDropoff.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentQuery = s.toString()
                applyFiltersAndSort()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun applyFiltersAndSort() {
        // 1. Filter berdasarkan Search
        var list = if (currentQuery.isEmpty()) {
            fullLocationList
        } else {
            fullLocationList.filter { 
                it.name.contains(currentQuery, ignoreCase = true) || 
                it.address.contains(currentQuery, ignoreCase = true)
            }
        }

        // 2. Apply Sorting
        list = when (currentSortMode) {
            "proximity" -> list.sortedBy { calculateDistance(it) }
            "newest" -> list.reversed()
            else -> list
        }

        adapter?.updateData(list, userLocation)
        updateMapMarkers(list)
    }

    private fun calculateDistance(loc: Locations): Float {
        if (userLocation == null) return Float.MAX_VALUE
        val results = FloatArray(1)
        Location.distanceBetween(
            userLocation!!.latitude, userLocation!!.longitude,
            loc.latitude, loc.longitude,
            results
        )
        return results[0]
    }

    private fun updateMapMarkers(locations: List<Locations>) {
        mInterfaceMap?.clear()
        userLocation?.let {
            mInterfaceMap?.addMarker(MarkerOptions()
                .position(it)
                .title("Lokasi Anda")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
        }
        for (loc in locations) {
            val pos = LatLng(loc.latitude, loc.longitude)
            mInterfaceMap?.addMarker(MarkerOptions().position(pos).title(loc.name).snippet(loc.address))
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mInterfaceMap = googleMap
        googleMap.uiSettings.isZoomControlsEnabled = true
        moveCameraToUserLocation()
    }

    private fun moveCameraToUserLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mInterfaceMap?.isMyLocationEnabled = true
            val cts = CancellationTokenSource()
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        userLocation = LatLng(location.latitude, location.longitude)
                        userLocation?.let {
                            mInterfaceMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
                            applyFiltersAndSort()
                        }
                    }
                }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun getDropoffDataFromBackend() {
        val apiService = RetrofitClient.locationService
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = apiService.getAllLocations()
                if (response.isSuccessful && response.body() != null) {
                    fullLocationList = response.body()!!
                    adapter = DropoffAdapter(fullLocationList)
                    binding.rvNearbyDropoff.adapter = adapter
                    applyFiltersAndSort()
                }
            } catch (e: Exception) { }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
