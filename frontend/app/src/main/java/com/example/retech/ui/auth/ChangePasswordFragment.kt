package com.example.retech.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.retech.databaseViewModel.UserViewModel
import com.example.retech.databinding.FragmentChangePasswordBinding
import com.example.retech.utils.SessionManager

class ChangePasswordFragment : Fragment() {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    private lateinit var userViewModel: UserViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        sessionManager = SessionManager(requireContext())

        binding.viewModel = userViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // Reset state before using
        userViewModel.resetChangePasswordState()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSavePassword.setOnClickListener {
            val email = sessionManager.getUserEmail()
            if (email.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Sesi tidak valid, harap relogin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            userViewModel.changePassword(email)
        }

        userViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnSavePassword.text = if (isLoading) "" else "Save Password"
            binding.btnSavePassword.isEnabled = !isLoading
        }

        userViewModel.changePasswordSuccess.observe(viewLifecycleOwner) { success ->
            if (success == true) {
                Toast.makeText(requireContext(), "Password berhasil diubah", Toast.LENGTH_SHORT).show()
                userViewModel.resetChangePasswordState()
                findNavController().popBackStack()
            }
        }

        userViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null && userViewModel.changePasswordSuccess.value == false) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
