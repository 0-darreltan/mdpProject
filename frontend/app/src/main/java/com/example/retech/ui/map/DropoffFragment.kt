package com.example.retech.ui.map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.retech.R
import com.example.retech.databinding.FragmentDropoffBinding

class DropoffFragment : Fragment() {

    private var _binding: FragmentDropoffBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDropoffBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvSortDropoff.setOnClickListener {
            val popup = androidx.appcompat.widget.PopupMenu(requireContext(), view)

            popup.menuInflater.inflate(R.menu.sort_menu, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.sort_proximity -> {
                        binding.tvSortDropoff.text = "Sort by\nProximity"
                        // Jalankan fungsi filter data berdasarkan jarak terdekat di sini
                        true
                    }
                    R.id.sort_newest -> {
                        binding.tvSortDropoff.text = "Sort by\nNewest"
                        true
                    }
                    R.id.sort_popular -> {
                        binding.tvSortDropoff.text = "Sort by\nPopular"
                        true
                    }
                    else -> false
                }
            }

            popup.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}