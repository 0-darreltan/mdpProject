package com.example.retech.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.retech.R
import com.example.retech.databinding.FragmentHomeBinding
import com.example.retech.utils.SessionManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationTokenSource

class HomeFragment : Fragment(R.layout.fragment_home), OnMapReadyCallback {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private var mInterfaceMap: GoogleMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            moveCameraToUserLocation()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val mapFragment = childFragmentManager.findFragmentById(R.id.googleMapHome) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val userName = sessionManager.getUserName() ?: "User"

        val navOptionsToRight = NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left)
            .setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right)
            .build()

        binding.tvHome1.text = "Welcome Back, $userName."

        binding.tvGlobalMap.setOnClickListener {
            findNavController().navigate(
                R.id.action_homeFragment_to_dropoffMapFragment,
                null,
                navOptionsToRight
            )
        }

        binding.btnDropOffHome.setOnClickListener {
            findNavController().navigate(
                R.id.action_homeFragment_to_dropoffMapFragment,
                null,
                navOptionsToRight
            )
        }

        binding.btnAddDeviceHome.setOnClickListener {
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
                        val userCoordinates = LatLng(location.latitude, location.longitude)
                        mInterfaceMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(userCoordinates, 15f))
                        mInterfaceMap?.clear()
                        mInterfaceMap?.addMarker(MarkerOptions().position(userCoordinates).title("Lokasi Anda"))
                    }
                }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
