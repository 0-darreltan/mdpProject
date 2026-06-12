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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.retech.R
import com.example.retech.data.remote.RetrofitClient
import com.example.retech.data.remote.api.GoogleAuthRequest
import com.example.retech.databinding.FragmentLoginBinding
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

class LoginFragment : Fragment(R.layout.fragment_login) {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

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

        binding.tvLogin4.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.btnLoginGoogle.setOnClickListener {
            jalankanGoogleSignIn()
        }
    }

    private fun jalankanGoogleSignIn() {
        // Use androidx.credentials.CredentialManager (NOT android.credentials)
        val credentialManager = CredentialManager.create(requireContext())

        val serverClientId = "710758978133-r3so0vmkncr4uqmrm67akb62hbmctjnr.apps.googleusercontent.com"

        // Configure Google ID Option
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .setAutoSelectEnabled(false)
            .build()

        // Build the request
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        // Execute asynchronously
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(requireContext(), request)
                val credential = result.credential

                when {
                    credential is GoogleIdTokenCredential -> {
                        val emailUser = credential.id
                        val namaUser = credential.displayName ?: "User ReTech"
                        Log.d("ReTechGoogleAuth", "Success! Name: $namaUser, Email: $emailUser")
                        kirimDataKeBackendExpress(namaUser, emailUser)
                    }
                    credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL -> {
                        // Path via CustomCredential (versi library 1.1.x)
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val emailUser = googleIdTokenCredential.id
                        val namaUser = googleIdTokenCredential.displayName ?: "User ReTech"
                        Log.d("ReTechGoogleAuth", "Success! Name: $namaUser, Email: $emailUser")
                        kirimDataKeBackendExpress(namaUser, emailUser)
                    }
                    else -> {
                        Log.e("ReTechGoogleAuth", "Tipe tidak dikenali: ${credential.type}")
                        Toast.makeText(context, "Sign in gagal: tipe akun tidak didukung", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("ReTechGoogleAuth", "Failed: ${e.message}")
                Toast.makeText(context, "Sign in cancelled or failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun kirimDataKeBackendExpress(nama: String, email: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val requestBody = GoogleAuthRequest(name = nama, email = email)
                val response = RetrofitClient.instance.loginRegisterWithGoogle(requestBody)

                if (response.isSuccessful && response.body() != null) {
                    val authData = response.body()!!
                    val tokenJWT = authData.token

                    // TODO: Simpan tokenJWT ke SharedPreferences / SessionManager agar user tetap login

                    Toast.makeText(context, "Login Sukses! Selamat datang ${authData.user?.name}", Toast.LENGTH_SHORT).show()

                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                } else {
                    Toast.makeText(context, "Gagal sinkronisasi ke server ReTech", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("RetrofitError", "Koneksi Gagal: ${e.message}")
                Toast.makeText(context, "Server tidak merespons, cek koneksi internet/IP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}