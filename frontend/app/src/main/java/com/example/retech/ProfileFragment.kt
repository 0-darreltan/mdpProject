package com.example.retech

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.retech.data.remote.RetrofitClient
import com.example.retech.databaseViewModel.DeviceViewModel
import com.example.retech.databaseViewModel.UserViewModel
import com.example.retech.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import com.example.retech.utils.SessionManager

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private lateinit var deviceViewModel: DeviceViewModel
    private lateinit var userViewModel: UserViewModel
    private var pendingBase64Image: String? = null

    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        if (bitmap != null) {
            uploadBitmap(bitmap)
        }
    }

    private val pickGalleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    uploadBitmap(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadBitmap(bitmap: Bitmap) {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        val byteArray = outputStream.toByteArray()
        pendingBase64Image = "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.NO_WRAP)
        val email = sessionManager.getUserEmail() ?: return
        userViewModel.updateProfilePicture(email, pendingBase64Image!!)
    }

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
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        // Menampilkan data user
        val userName = sessionManager.getUserName() ?: "User Name"
        val userEmail = sessionManager.getUserEmail() ?: "user@example.com"
        val profilePic = sessionManager.getProfilePicture()

        binding.tvUserName.text = userName
        binding.tvUserEmail.text = userEmail
        
        if (!profilePic.isNullOrEmpty()) {
            Glide.with(this)
                .load(profilePic)
                .placeholder(android.R.drawable.ic_menu_camera)
                .into(binding.ivProfilePicture)
        }
        // Menampilkan Total Devices
        val userId = sessionManager.getUserId() ?: ""
        
        // Hide change password jika login dengan Google, dan setup click picture
        if (userId.startsWith("google_")) {
            binding.btnChangePassword.visibility = View.GONE
        } else {
            binding.btnChangePassword.visibility = View.VISIBLE
            binding.btnChangePassword.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_changePasswordFragment)
            }
            
            // Allow manual users to change profile picture
            binding.ivProfilePicture.setOnClickListener {
                showImagePickerDialog()
            }
        }

        userViewModel.updateProfilePictureSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess == true) {
                Toast.makeText(requireContext(), "Profile picture updated", Toast.LENGTH_SHORT).show()
                pendingBase64Image?.let {
                    sessionManager.saveProfilePicture(it)
                    Glide.with(this)
                        .load(it)
                        .placeholder(android.R.drawable.ic_menu_camera)
                        .into(binding.ivProfilePicture)
                }
                userViewModel.resetUpdateProfilePictureState()
            } else if (isSuccess == false) {
                Toast.makeText(requireContext(), userViewModel.error.value ?: "Failed to update", Toast.LENGTH_SHORT).show()
                userViewModel.resetUpdateProfilePictureState()
            }
        }

        deviceViewModel.setUserId(userId)
        deviceViewModel.totalDeviceCount.observe(viewLifecycleOwner) { count ->
            binding.tvTotalDevices.text = count.toString()
        }

        binding.cvTotalDevices.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_inventoryFragment)
        }

        // Cek jika Admin
        if (sessionManager.getUserRole() == "admin") {
            binding.cvTotalDropoff.visibility = View.VISIBLE
            binding.cvTotalGuides.visibility = View.VISIBLE
            
            binding.cvTotalDropoff.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_manageDropoffFragment)
            }
            
            binding.cvTotalGuides.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_manageGuideFragment)
            }
            
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val dropoffResponse = RetrofitClient.locationService.getAllLocations()
                    if (dropoffResponse.isSuccessful && dropoffResponse.body() != null) {
                        binding.tvTotalDropoff.text = dropoffResponse.body()!!.size.toString()
                    }
                    
                    val guides = RetrofitClient.guideService.getGuides()
                    binding.tvTotalGuides.text = guides.size.toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
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

    private fun showImagePickerDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        AlertDialog.Builder(requireContext())
            .setTitle("Update Profile Picture")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> takePhotoLauncher.launch(null)
                    1 -> pickGalleryLauncher.launch("image/*")
                }
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}