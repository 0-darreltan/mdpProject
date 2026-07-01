package com.example.retech

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.retech.databaseViewModel.DeviceViewModel
import com.example.retech.databinding.FragmentProfileBinding
import com.example.retech.utils.SessionManager

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private lateinit var deviceViewModel: DeviceViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        deviceViewModel = ViewModelProvider(requireActivity())[DeviceViewModel::class.java]

        // Menampilkan data user
        val userName = sessionManager.getUserName() ?: "User Name"
        val userEmail = sessionManager.getUserEmail() ?: "user@example.com"

        binding.tvUserName.text = userName
        binding.tvUserEmail.text = userEmail
        
        // Menampilkan Total Devices
        val userId = sessionManager.getUserId() ?: ""
        
        // Hide change password jika login dengan Google
        if (userId.startsWith("google_")) {
            binding.btnChangePassword.visibility = View.GONE
        } else {
            binding.btnChangePassword.visibility = View.VISIBLE
            binding.btnChangePassword.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_changePasswordFragment)
            }
        }

        deviceViewModel.setUserId(userId)
        deviceViewModel.totalDeviceCount.observe(viewLifecycleOwner) { count ->
            binding.tvTotalDevices.text = count.toString()
        }

        // Logout
        binding.btnLogout.setOnClickListener {
            sessionManager.logout()
            
            // Navigate kembali ke LoginFragment dan menghapus history fragment sebelumnya
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.nav_graph, true)
                .build()
            findNavController().navigate(R.id.loginFragment, null, navOptions)
            
            Toast.makeText(requireContext(), "Berhasil logout", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}