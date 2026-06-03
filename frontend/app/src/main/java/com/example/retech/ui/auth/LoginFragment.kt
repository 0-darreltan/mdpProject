package com.example.retech.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.retech.R
import com.example.retech.utils.SessionManager

class LoginFragment : Fragment(R.layout.fragment_login) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager(requireContext())

        // Cek Status Sesi User
        if (sessionManager.isLoggedIn()) {
            // Jika sudah login, langsung lempar ke Dashboard (Home)
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        }
    }
}