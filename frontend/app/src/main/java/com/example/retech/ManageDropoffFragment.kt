package com.example.retech

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retech.data.remote.RetrofitClient
import com.example.retech.databaseModel.Locations
import com.example.retech.databinding.FragmentManageDropoffBinding
import com.example.retech.ui.map.DropoffAdapter
import kotlinx.coroutines.launch

class ManageDropoffFragment : Fragment() {

    private var _binding: FragmentManageDropoffBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var adapter: DropoffAdapter
    private var selectedLocationId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManageDropoffBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        fetchLocations()
        
        binding.btnSave.setOnClickListener {
            saveLocation()
        }
        
        binding.btnClear.setOnClickListener {
            clearForm()
        }
        
        binding.btnDelete.setOnClickListener {
            deleteLocation()
        }
    }
    
    private fun setupRecyclerView() {
        adapter = DropoffAdapter(emptyList()) { location ->
            populateForm(location)
        }
        binding.rvDropoff.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDropoff.adapter = adapter
    }
    
    private fun fetchLocations() {
        binding.progressBar.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.locationService.getAllLocations()
                if (response.isSuccessful && response.body() != null) {
                    adapter.updateList(response.body()!!)
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch locations", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("ManageDropoff", "Error fetching locations", e)
                Toast.makeText(requireContext(), "Error fetching locations", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }
    
    private fun populateForm(location: Locations) {
        selectedLocationId = location._id
        
        binding.etName.setText(location.name)
        binding.etAddress.setText(location.address)
        binding.etAcceptedItems.setText(location.accepted_items.joinToString(", "))
        binding.etLatitude.setText(location.latitude.toString())
        binding.etLongitude.setText(location.longitude.toString())
        binding.etImageUrl.setText(location.image_url ?: "")
        
        binding.btnSave.text = "Update"
        binding.btnDelete.visibility = View.VISIBLE
    }
    
    private fun clearForm() {
        selectedLocationId = null
        
        binding.etName.text?.clear()
        binding.etAddress.text?.clear()
        binding.etAcceptedItems.text?.clear()
        binding.etLatitude.text?.clear()
        binding.etLongitude.text?.clear()
        binding.etImageUrl.text?.clear()
        
        binding.btnSave.text = "Add"
        binding.btnDelete.visibility = View.GONE
    }
    
    private fun saveLocation() {
        val name = binding.etName.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val acceptedItemsStr = binding.etAcceptedItems.text.toString().trim()
        val latitudeStr = binding.etLatitude.text.toString().trim()
        val longitudeStr = binding.etLongitude.text.toString().trim()
        val imageUrl = binding.etImageUrl.text.toString().trim()
        
        if (name.isEmpty() || address.isEmpty() || latitudeStr.isEmpty() || longitudeStr.isEmpty()) {
            Toast.makeText(requireContext(), "Name, Address, Latitude, and Longitude are required", Toast.LENGTH_SHORT).show()
            return
        }
        
        val latitude = latitudeStr.toDoubleOrNull()
        val longitude = longitudeStr.toDoubleOrNull()
        
        if (latitude == null || longitude == null) {
            Toast.makeText(requireContext(), "Invalid Latitude or Longitude", Toast.LENGTH_SHORT).show()
            return
        }
        
        val acceptedItems = acceptedItemsStr.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        
        val location = Locations(
            _id = selectedLocationId,
            name = name,
            address = address,
            accepted_items = acceptedItems,
            operational_hours = emptyList(), // Provide existing hours logic if you want to modify hours
            latitude = latitude,
            longitude = longitude,
            image_url = imageUrl.ifEmpty { null }
        )
        
        binding.progressBar.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                if (selectedLocationId == null) {
                    val response = RetrofitClient.locationService.addLocation(location)
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Location added successfully", Toast.LENGTH_SHORT).show()
                        clearForm()
                        fetchLocations()
                    } else {
                        Toast.makeText(requireContext(), "Failed to add location", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val response = RetrofitClient.locationService.updateLocation(selectedLocationId!!, location)
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Location updated successfully", Toast.LENGTH_SHORT).show()
                        clearForm()
                        fetchLocations()
                    } else {
                        Toast.makeText(requireContext(), "Failed to update location", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("ManageDropoff", "Error saving location", e)
                Toast.makeText(requireContext(), "Error saving location", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }
    
    private fun deleteLocation() {
        if (selectedLocationId == null) return
        
        binding.progressBar.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.locationService.deleteLocation(selectedLocationId!!)
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Location deleted successfully", Toast.LENGTH_SHORT).show()
                    clearForm()
                    fetchLocations()
                } else {
                    Toast.makeText(requireContext(), "Failed to delete location", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("ManageDropoff", "Error deleting location", e)
                Toast.makeText(requireContext(), "Error deleting location", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}