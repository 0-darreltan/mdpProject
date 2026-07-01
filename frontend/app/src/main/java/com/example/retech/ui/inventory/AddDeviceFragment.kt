package com.example.retech.ui.inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.retech.databaseModel.Device
import com.example.retech.databaseViewModel.DeviceViewModel
import com.example.retech.databinding.FragmentAddDeviceBinding
import com.example.retech.utils.SessionManager
import java.util.Calendar

class AddDeviceFragment : Fragment() {

    private var _binding: FragmentAddDeviceBinding? = null
    private val binding get() = _binding!!

    private lateinit var deviceViewModel: DeviceViewModel
    private lateinit var sessionManager: SessionManager

    private var selectedCategory: String = ""
    private var selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR)
    
    private var deviceToEdit: Device? = null

    private val categoryChips = mutableListOf<TextView>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddDeviceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        deviceViewModel = ViewModelProvider(requireActivity())[DeviceViewModel::class.java]
        binding.viewModel = deviceViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        deviceToEdit = arguments?.getSerializable("device_to_edit") as? Device

        setupCategoryChips()
        setupYearSpinner()
        
        if (deviceToEdit != null) {
            prefillDeviceData()
        }
        
        setupSaveButton()
        observeViewModel()
    }
    
    private fun prefillDeviceData() {
        deviceViewModel.newDeviceName.value = deviceToEdit!!.name
        
        val categories = listOf("Laptop", "Smartphone", "Tablet", "Monitor", "Peripheral")
        val index = categories.indexOf(deviceToEdit!!.category)
        if (index >= 0) {
            selectCategory(index, deviceToEdit!!.category)
        }

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = (currentYear downTo currentYear - 15).map { it.toString() }
        val yearIndex = years.indexOf(deviceToEdit!!.purchaseYear.toString())
        if (yearIndex >= 0) {
            binding.spinnerYear.setSelection(yearIndex)
        }

        binding.btnSaveDevice.text = "Update Device"
    }

    private fun setupCategoryChips() {
        // Collect all chip views
        categoryChips.addAll(
            listOf(
                binding.chipLaptop,
                binding.chipSmartphone,
                binding.chipTablet,
                binding.chipMonitor,
                binding.chipPeripheral
            )
        )

        // Set click listeners for each chip
        val categories = listOf("Laptop", "Smartphone", "Tablet", "Monitor", "Peripheral")

        categoryChips.forEachIndexed { index, chip ->
            chip.setOnClickListener {
                selectCategory(index, categories[index])
            }
        }

        // Select "Laptop" by default if not editing
        if (deviceToEdit == null) {
            selectCategory(0, "Laptop")
        }
    }

    private fun selectCategory(selectedIndex: Int, category: String) {
        selectedCategory = category

        categoryChips.forEachIndexed { index, chip ->
            if (index == selectedIndex) {
                chip.isSelected = true
                chip.setTextColor(0xFFFFFFFF.toInt())
            } else {
                chip.isSelected = false
                chip.setTextColor(0xFF1F2937.toInt())
            }
        }
    }

    private fun setupYearSpinner() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = (currentYear downTo currentYear - 15).map { it.toString() }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            years
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerYear.adapter = adapter
    }

    private fun setupSaveButton() {
        binding.btnSaveDevice.setOnClickListener {
            val deviceName = deviceViewModel.newDeviceName.value?.trim() ?: ""

            if (deviceName.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a device name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedCategory.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val yearStr = binding.spinnerYear.selectedItem?.toString() ?: ""
            if (yearStr.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a purchase year", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            selectedYear = yearStr.toInt()

            // Ambil userId dari session
            val userId = sessionManager.getUserId() ?: ""
            if (userId.isEmpty()) {
                Toast.makeText(requireContext(), "User session not found. Please login again.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Tentukan badge berdasarkan usia device
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val deviceAge = currentYear - selectedYear
            val badge = when {
                deviceAge >= 5 -> "Recycle Ready"
                deviceAge >= 3 -> "Low Waste"
                else -> "High Value"
            }

            // Tentukan condition berdasarkan usia
            val condition = when {
                deviceAge >= 5 -> "Fair"
                deviceAge >= 3 -> "Good"
                else -> "Mint"
            }

            if (deviceToEdit != null) {
                // Update existing device
                val updatedDevice = deviceToEdit!!.copy(
                    name = deviceName,
                    category = selectedCategory,
                    purchaseYear = selectedYear,
                    condition = condition,
                    badge = badge
                )
                deviceViewModel.updateDevice(updatedDevice)
            } else {
                // Insert new device
                val device = Device(
                    userId = userId,
                    name = deviceName,
                    category = selectedCategory,
                    purchaseYear = selectedYear,
                    condition = condition,
                    badge = badge
                )
                deviceViewModel.insertDevice(device)
            }
        }
    }

    private fun observeViewModel() {
        // Observe hasil insert atau update
        deviceViewModel.insertResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                val msg = if (deviceToEdit != null) "Device updated successfully!" else "Device saved successfully!"
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                deviceViewModel.resetInsertResult()
                findNavController().navigateUp()
            }
        }

        // Observe error
        deviceViewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        deviceViewModel.newDeviceName.value = "" // clear for next use
    }
}