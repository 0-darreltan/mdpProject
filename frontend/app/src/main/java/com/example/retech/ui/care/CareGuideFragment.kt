package com.example.retech.ui.care

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.retech.R
import com.example.retech.databinding.FragmentCareGuideBinding
import com.example.retech.utils.SessionManager

class CareGuideFragment : Fragment() {

    private var _binding: FragmentCareGuideBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: GuideViewModel
    private lateinit var adapter: GuideAdapter
    private lateinit var sessionManager: SessionManager

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
        binding.rvGuides.isNestedScrollingEnabled = false
        binding.rvGuides.adapter = adapter

        sessionManager = SessionManager(requireContext())
        val profilePic = sessionManager.getProfilePicture()
        if (!profilePic.isNullOrEmpty()) {
            Glide.with(this)
                .load(profilePic)
                .placeholder(R.mipmap.ic_launcher)
                .into(binding.ivProfileCareGuide)
        }

        binding.cvProfileCareGuide.setOnClickListener {
            findNavController().navigate(R.id.action_careGuideFragment_to_profileFragment)
        }

        // Setup ViewModel
        viewModel = ViewModelProvider(this)[GuideViewModel::class.java]

        // Observe Data
        viewModel.guides.observe(viewLifecycleOwner) { guides ->
            adapter.updateData(guides)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Use progressBar if it's back, or omit
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            // Use error view if it's back, or omit
        }

        // Fetch Data
        viewModel.fetchGuides()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}