package com.example.retech.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.retech.R
import com.example.retech.databaseViewModel.UserViewModel
import com.example.retech.databinding.FragmentNewPasswordBinding

class NewPasswordFragment : Fragment(R.layout.fragment_new_password) {
    private var _binding: FragmentNewPasswordBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPasswordBinding.inflate(inflater, container, false)
        binding.viewModel = userViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()

        binding.btnUpdatePassword.setOnClickListener {
            // Reset error messages
            binding.tilToken.error = null
            binding.tilNewPassword.error = null
            binding.tilConfirmPassword.error = null

            userViewModel.resetPassword()
        }

        binding.tvBackToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_newPasswordFragment_to_loginFragment)
        }
    }

    private fun setupObservers() {
        userViewModel.resetSuccess.observe(viewLifecycleOwner) { success ->
            if (success == true) {
                Toast.makeText(requireContext(), "Password berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_newPasswordFragment_to_loginFragment)
            }
        }

        userViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnUpdatePassword.isEnabled = !isLoading
            binding.btnUpdatePassword.text = if (isLoading) "Updating..." else "Update Password"
        }

        userViewModel.error.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
