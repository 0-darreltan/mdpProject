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
import com.example.retech.databaseModel.Guide
import com.example.retech.databinding.FragmentManageGuideBinding
import com.example.retech.ui.care.GuideAdapter
import kotlinx.coroutines.launch

class ManageGuideFragment : Fragment() {

    private var _binding: FragmentManageGuideBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var adapter: GuideAdapter
    private var selectedGuideId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManageGuideBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        fetchGuides()
        
        binding.btnSave.setOnClickListener {
            saveGuide()
        }
        
        binding.btnClear.setOnClickListener {
            clearForm()
        }
        
        binding.btnDelete.setOnClickListener {
            deleteGuide()
        }
    }
    
    private fun setupRecyclerView() {
        adapter = GuideAdapter(emptyList()) { guide ->
            populateForm(guide)
        }
        binding.rvGuide.layoutManager = LinearLayoutManager(requireContext())
        binding.rvGuide.adapter = adapter
    }
    
    private fun fetchGuides() {
        binding.progressBar.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val guides = RetrofitClient.guideService.getGuides()
                adapter.updateData(guides)
            } catch (e: Exception) {
                Log.e("ManageGuide", "Error fetching guides", e)
                Toast.makeText(requireContext(), "Failed to fetch guides", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }
    
    private fun populateForm(guide: Guide) {
        selectedGuideId = guide._id
        
        binding.etName.setText(guide.name)
        binding.etCategory.setText(guide.category)
        binding.etSummary.setText(guide.summary)
        binding.etFileUrl.setText(guide.file_url)
        binding.etImageUrl.setText(guide.image_url)
        
        binding.btnSave.text = "Update"
        binding.btnDelete.visibility = View.VISIBLE
    }
    
    private fun clearForm() {
        selectedGuideId = null
        
        binding.etName.text?.clear()
        binding.etCategory.text?.clear()
        binding.etSummary.text?.clear()
        binding.etFileUrl.text?.clear()
        binding.etImageUrl.text?.clear()
        
        binding.btnSave.text = "Add"
        binding.btnDelete.visibility = View.GONE
    }
    
    private fun saveGuide() {
        val name = binding.etName.text.toString().trim()
        val category = binding.etCategory.text.toString().trim()
        val summary = binding.etSummary.text.toString().trim()
        val fileUrl = binding.etFileUrl.text.toString().trim()
        val imageUrl = binding.etImageUrl.text.toString().trim()
        
        if (name.isEmpty() || category.isEmpty() || summary.isEmpty() || fileUrl.isEmpty() || imageUrl.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        
        val guide = Guide(
            _id = selectedGuideId ?: "",
            name = name,
            category = category,
            summary = summary,
            file_url = fileUrl,
            image_url = imageUrl
        )
        
        binding.progressBar.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                if (selectedGuideId == null) {
                    val response = RetrofitClient.guideService.addGuide(guide)
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Guide added successfully", Toast.LENGTH_SHORT).show()
                        clearForm()
                        fetchGuides()
                    } else {
                        Toast.makeText(requireContext(), "Failed to add guide", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val response = RetrofitClient.guideService.updateGuide(selectedGuideId!!, guide)
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Guide updated successfully", Toast.LENGTH_SHORT).show()
                        clearForm()
                        fetchGuides()
                    } else {
                        Toast.makeText(requireContext(), "Failed to update guide", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("ManageGuide", "Error saving guide", e)
                Toast.makeText(requireContext(), "Error saving guide", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }
    
    private fun deleteGuide() {
        if (selectedGuideId == null) return
        
        binding.progressBar.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.guideService.deleteGuide(selectedGuideId!!)
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Guide deleted successfully", Toast.LENGTH_SHORT).show()
                    clearForm()
                    fetchGuides()
                } else {
                    Toast.makeText(requireContext(), "Failed to delete guide", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("ManageGuide", "Error deleting guide", e)
                Toast.makeText(requireContext(), "Error deleting guide", Toast.LENGTH_SHORT).show()
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