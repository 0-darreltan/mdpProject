package com.example.retech.ui.care

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retech.databinding.FragmentCareGuideBinding

class CareGuideFragment : Fragment() {

    private var _binding: FragmentCareGuideBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: GuideViewModel
    private lateinit var adapter: GuideAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCareGuideBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        adapter = GuideAdapter(emptyList())
        binding.rvGuides.layoutManager = LinearLayoutManager(requireContext())
        binding.rvGuides.adapter = adapter

        // Setup ViewModel
        viewModel = ViewModelProvider(this)[GuideViewModel::class.java]

        // Observe Data
        viewModel.guides.observe(viewLifecycleOwner) { guides ->
            adapter.updateData(guides)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = errorMessage
            } else {
                binding.tvError.visibility = View.GONE
            }
        }

        // Fetch Data
        viewModel.fetchGuides()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}