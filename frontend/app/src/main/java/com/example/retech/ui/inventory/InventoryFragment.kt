package com.example.retech.ui.inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.retech.R
import com.example.retech.databaseModel.Device
import com.example.retech.databaseViewModel.DeviceViewModel
import com.example.retech.databinding.FragmentInventoryBinding
import com.example.retech.utils.SessionManager

class InventoryFragment : Fragment() {

    private var _binding: FragmentInventoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var deviceViewModel: DeviceViewModel
    private lateinit var deviceAdapter: DeviceAdapter
    private lateinit var sessionManager: SessionManager

    // Filter configurations
    private var allDevices: List<Device> = emptyList()
    private var currentFilter = DeviceFilter.ALL

    // Enum for easy filter additions
    enum class DeviceFilter(val label: String) {
        ALL("All Devices"),
        CATEGORY_LAPTOP("Category: Laptop"),
        CATEGORY_SMARTPHONE("Category: Smartphone"),
        CATEGORY_TABLET("Category: Tablet"),
        CATEGORY_MONITOR("Category: Monitor"),
        CATEGORY_PERIPHERAL("Category: Peripheral"),
        HIGH_VALUE("High Value"),
        LOW_VALUE("Low Value"), // Displayed as Low Value, maps to "Low Waste" badge logic
        RECYCLE_READY("Recycle Ready")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        deviceViewModel = ViewModelProvider(requireActivity())[DeviceViewModel::class.java]

        setupRecyclerView()
        setupClickListeners()
        setupFilterMenu()

        // Set userId dari session agar ViewModel memfilter per user
        val userId = sessionManager.getUserId() ?: ""
        deviceViewModel.setUserId(userId)

        val profilePic = sessionManager.getProfilePicture()
        if (!profilePic.isNullOrEmpty()) {
            Glide.with(this)
                .load(profilePic)
                .placeholder(R.mipmap.ic_launcher)
                .into(binding.ivProfileInventory)
        }

        observeViewModel()
    }

    private fun setupRecyclerView() {
        deviceAdapter = DeviceAdapter(
            onEditClick = { device ->
                val bundle = Bundle().apply {
                    putSerializable("device_to_edit", device)
                }
                findNavController().navigate(R.id.action_inventoryFragment_to_addDeviceFragment, bundle)
            },
            onDeleteClick = { device ->
                deviceViewModel.deleteDevice(device)
            }
        )
        binding.rvDevices.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = deviceAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun observeViewModel() {
        // Observe daftar device milik user
        deviceViewModel.devices.observe(viewLifecycleOwner) { devices ->
            allDevices = devices
            applyFilter()
        }

        // Observe total device count
        deviceViewModel.totalDeviceCount.observe(viewLifecycleOwner) { count ->
            binding.tvTotalAssets.text = count.toString()
            binding.tvNewThisMonth.text = "+${count.coerceAtMost(5)} this month"
        }

        // Observe recycle ready count
        deviceViewModel.recycleReadyCount.observe(viewLifecycleOwner) { count ->
            binding.tvRecyclingReady.text = count.toString()
            if (count > 0) {
                binding.tvUrgentLabel.visibility = View.VISIBLE
            } else {
                binding.tvUrgentLabel.visibility = View.GONE
            }
        }
    }

    private fun setupFilterMenu() {
        // Initial label
        binding.tvFilterByType.text = currentFilter.label

        val clickListener = View.OnClickListener { view ->
            val popup = PopupMenu(requireContext(), binding.ivFilterIcon)
            
            // Populate menu with filter options
            DeviceFilter.values().forEachIndexed { index, filter ->
                popup.menu.add(0, index, 0, filter.label)
            }
            
            popup.setOnMenuItemClickListener { item ->
                val selectedFilter = DeviceFilter.values()[item.itemId]
                if (currentFilter != selectedFilter) {
                    currentFilter = selectedFilter
                    applyFilter()
                }
                true
            }
            popup.show()
        }

        // Allow clicking either the text or icon to show the filter popup
        binding.ivFilterIcon.setOnClickListener(clickListener)
        binding.tvFilterByType.setOnClickListener(clickListener)
    }

    private fun applyFilter() {
        binding.tvFilterByType.text = currentFilter.label
        
        val filteredList = when (currentFilter) {
            DeviceFilter.ALL -> allDevices
            DeviceFilter.CATEGORY_LAPTOP -> allDevices.filter { it.category.equals("Laptop", ignoreCase = true) }
            DeviceFilter.CATEGORY_SMARTPHONE -> allDevices.filter { it.category.equals("Smartphone", ignoreCase = true) }
            DeviceFilter.CATEGORY_TABLET -> allDevices.filter { it.category.equals("Tablet", ignoreCase = true) }
            DeviceFilter.CATEGORY_MONITOR -> allDevices.filter { it.category.equals("Monitor", ignoreCase = true) }
            DeviceFilter.CATEGORY_PERIPHERAL -> allDevices.filter { it.category.equals("Peripheral", ignoreCase = true) }
            
            DeviceFilter.HIGH_VALUE -> allDevices.filter { it.badge.equals("High Value", ignoreCase = true) }
            DeviceFilter.LOW_VALUE -> allDevices.filter { it.badge.equals("Low Waste", ignoreCase = true) }
            DeviceFilter.RECYCLE_READY -> allDevices.filter { it.badge.equals("Recycle Ready", ignoreCase = true) }
        }
        
        deviceAdapter.updateList(filteredList)
        updateEmptyState(filteredList.isEmpty())
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.rvDevices.visibility = View.GONE
            binding.llEmptyState.visibility = View.VISIBLE
        } else {
            binding.rvDevices.visibility = View.VISIBLE
            binding.llEmptyState.visibility = View.GONE
        }
    }

    private fun setupClickListeners() {
        // Navigate to Add Device
        binding.btnAddDevice.setOnClickListener {
            findNavController().navigate(R.id.action_inventoryFragment_to_addDeviceFragment)
        }
        
        binding.cvProfileInventory.setOnClickListener {
            findNavController().navigate(R.id.action_inventoryFragment_to_profileFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}