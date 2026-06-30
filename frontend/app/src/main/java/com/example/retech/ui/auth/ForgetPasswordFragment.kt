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
import com.example.retech.databinding.FragmentForgetPasswordBinding

class ForgetPasswordFragment : Fragment(R.layout.fragment_forget_password) {
    private var _binding: FragmentForgetPasswordBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnSendForget.isEnabled = !isLoading
            binding.btnSendForget.text = if (isLoading) "Sending..." else "Send Reset Link"
        }

        // Observer untuk status sukses forget password
        userViewModel.forgotPasswordSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                // Berhasil: Langsung direct ke NewPasswordFragment
                findNavController().navigate(R.id.action_forgetPasswordFragment_to_newPasswordFragment)
            }
        }

        userViewModel.error.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            }
        }

        binding.btnSendForget.setOnClickListener {
            val email = binding.etEmailLogin.text.toString().trim()
            if (email.isEmpty()) {
                binding.etEmailLogin.error = "Email wajib diisi"
                return@setOnClickListener
            }
            userViewModel.forgotPassword(email)
        }

        binding.btnBackForget.setOnClickListener {
            findNavController().navigate(R.id.action_forgetPasswordFragment_to_loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
