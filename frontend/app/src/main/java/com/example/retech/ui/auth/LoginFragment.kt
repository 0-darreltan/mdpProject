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
import com.example.retech.databinding.FragmentLoginBinding
import com.example.retech.utils.SessionManager
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

class LoginFragment : Fragment(R.layout.fragment_login) {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        setupObservers()

        binding.tvLogin4.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.btnLoginEmail.setOnClickListener {
            val email = binding.etEmailLogin.text.toString().trim()
            val password = binding.etPassLogin.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Harap isi email dan password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            userViewModel.login(email, password)
        }

        binding.btnLoginGoogle.setOnClickListener {
            jalankanGoogleSignIn()
        }

        binding.tvForgotPass.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgetPasswordFragment)
        }
    }

    private fun setupObservers() {
        userViewModel.authResult.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                val user = response.user
                if (user != null) {
                    sessionManager.saveSession(
                        user._id ?: "",
                        user.name ?: "",
                        user.email ?: "",
                    )
                }
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            }
        }

        userViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnLoginEmail.isEnabled = !isLoading
            binding.btnLoginGoogle.isEnabled = !isLoading
        }

        userViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun jalankanGoogleSignIn() {
        val credentialManager = CredentialManager.create(requireContext())

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
                        Log.e("ReTechGoogleAuth", "Tipe tidak dikenali")
                        Toast.makeText(context, "Sign in gagal: tipe akun tidak didukung", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("ReTechGoogleAuth", "Failed: ${e.message}")
                Toast.makeText(context, "Sign in cancelled or failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
