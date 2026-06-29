package com.example.retech.ui.map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.retech.databinding.FragmentDropoffDetailBinding

class DropoffDetailFragment : Fragment() {
    private var _binding: FragmentDropoffDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDropoffDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val locationData = arguments?.getSerializable("locationData") as? com.example.retech.databaseModel.Locations

        locationData?.let { data ->
            binding.tvNamaDropoffDetail.text = data.name
            binding.tvAlamatDropoffDetail.text = data.address
            
            // Set up RecyclerView for accepted items
            if (data.accepted_items.isNotEmpty()) {
                val adapter = AcceptedItemsAdapter(data.accepted_items)
                binding.rvAcceptedDetail.layoutManager = GridLayoutManager(requireContext(), 2)
                binding.rvAcceptedDetail.adapter = adapter
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}