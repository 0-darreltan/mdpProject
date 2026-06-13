package com.example.retech.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.retech.R
import com.example.retech.databaseViewModel.UserViewModel
import com.example.retech.databinding.FragmentRegisterBinding
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()

        binding.tvRegister4.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.btnRegistEmail.setOnClickListener {
            val nama = binding.etNamaRegist.text.toString().trim()
            val email = binding.etEmailRegist.text.toString().trim()
            val password = binding.etPassRegist.text.toString().trim()

            if (nama.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Harap isi semua bidang", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            userViewModel.register(nama, email, password)
        }

        binding.btnRegistGoogle.setOnClickListener {
            jalankanGoogleRegister()
        }
    }

    private fun setupObservers() {
        userViewModel.authResult.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                Toast.makeText(context, "Registrasi Berhasil! Selamat datang ${response.user?.name}", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
            }
        }

        userViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnRegistEmail.isEnabled = !isLoading
            binding.btnRegistGoogle.isEnabled = !isLoading
        }

        userViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun jalankanGoogleRegister() {
        val credentialManager = CredentialManager.create(requireContext())

        // Gunakan Web Client ID yang sama dengan Login
        val serverClientId = "564624439884-1d3cpctd5us8sgpsu7hr2i7kldogj12e.apps.googleusercontent.com"

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(requireContext(), request)
                val credential = result.credential

                when {
                    credential is GoogleIdTokenCredential -> {
                        val emailUser = credential.id
                        val namaUser = credential.displayName ?: "User ReTech"
                        userViewModel.loginWithGoogle(namaUser, emailUser)
                    }
                    credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL -> {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val emailUser = googleIdTokenCredential.id
                        val namaUser = googleIdTokenCredential.displayName ?: "User ReTech"
                        userViewModel.loginWithGoogle(namaUser, emailUser)
                    }
                    else -> {
                        Toast.makeText(requireContext(), "Tipe akun tidak didukung", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("ReTechGoogleRegist", "Gagal: ${e.message}")
                Toast.makeText(requireContext(), "Gagal Login Google", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
