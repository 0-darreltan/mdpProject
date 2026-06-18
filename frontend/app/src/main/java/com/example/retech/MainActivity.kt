package com.example.retech

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController // Tambahkan import ini jika belum ada
import com.example.retech.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView // Tambahkan import ini

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupActionBarWithNavController(navController)

        val bottomNav: BottomNavigationView = binding.bottomNavigation

        bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment, R.id.registerFragment -> {
                    supportActionBar?.hide()
                    binding.toolbar.visibility = View.GONE
                    bottomNav.visibility = View.GONE
                }

                R.id.homeFragment -> {
                    supportActionBar?.hide()
                    binding.toolbar.visibility = View.GONE
                    bottomNav.visibility = View.VISIBLE
                }

                // 3. Tambahkan ID fragment utama kalian yang lain di bawah sini jika ada (Inventory, dll.)
                // Supaya saat berpindah ke tab tersebut, footer bawahnya tetap konsisten kelihatan.
                // R.id.inventoryFragment, R.id.impactFragment -> {
                //     supportActionBar?.hide()
                //     binding.toolbar.visibility = View.GONE
                //     bottomNav.visibility = View.VISIBLE
                // }

                else -> {
                    supportActionBar?.show()
                    binding.toolbar.visibility = View.VISIBLE
                    bottomNav.visibility = View.GONE
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}