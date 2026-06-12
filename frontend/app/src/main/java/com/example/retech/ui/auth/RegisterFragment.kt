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
import com.example.retech.databinding.FragmentRegisterBinding
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

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

        binding.tvRegister4.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.btnRegistEmail.setOnClickListener {
            val nama = binding.etNamaRegist.text.toString()
            val email = binding.etEmailRegist.text.toString()
            val password = binding.etPassRegist.text.toString()
            // Implementasi registrasi manual jika diperlukan
        }

        binding.btnRegistGoogle.setOnClickListener {
            jalankanGoogleRegister()
        }
    }

    private fun jalankanGoogleRegister() {
        val credentialManager = CredentialManager.create(requireContext())

        // Pastikan Client ID ini sesuai dengan yang ada di Google Cloud Console (Web Client ID)
        val serverClientId = "710758978133-r3so0vmkncr4uqmrm67akb62hbmctjnr.apps.googleusercontent.com"

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
                        // Path langsung (beberapa versi library)
                        val emailUser = credential.id
                        val namaUser = credential.displayName ?: "User ReTech"
                        Log.d("ReTechGoogleRegist", "Sukses Google! Nama: $namaUser, Email: $emailUser")
                        kirimDataKeBackendExpress(namaUser, emailUser)
                    }
                    credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL -> {
                        // Path via CustomCredential (versi library 1.1.x)
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val emailUser = googleIdTokenCredential.id
                        val namaUser = googleIdTokenCredential.displayName ?: "User ReTech"
                        Log.d("ReTechGoogleRegist", "Sukses Google! Nama: $namaUser, Email: $emailUser")
                        kirimDataKeBackendExpress(namaUser, emailUser)
                    }
                    else -> {
                        Log.e("ReTechGoogleRegist", "Tipe credential tidak dikenali: ${credential.type}")
                        Toast.makeText(requireContext(), "Gagal: Tipe akun tidak didukung (${credential.type})", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("ReTechGoogleRegist", "Gagal/Batal memilih Google: ${e.message}")
                Toast.makeText(requireContext(), "Gagal/Batal Login Google", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(requireContext(), "Login Sukses! Selamat datang ${authData.user?.name}", Toast.LENGTH_SHORT).show()
                    
                    // Navigasi ke Home
                    findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Gagal sinkronisasi"
                    Log.e("BackendError", "Error: $errorMsg")
                    Toast.makeText(requireContext(), "Gagal sinkronisasi ke server", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("RetrofitError", "Koneksi Gagal: ${e.message}")
                Toast.makeText(requireContext(), "Server tidak merespons. Cek koneksi/IP Backend.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
