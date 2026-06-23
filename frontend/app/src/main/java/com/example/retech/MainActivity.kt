package com.example.retech

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
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

        val menuOrder = listOf(
            R.id.homeFragment,
            R.id.inventoryFragment,
            R.id.dropoffFragment,
            R.id.careGuideFragment
        )

        bottomNav.setOnItemSelectedListener { item ->
            val currentId = navController.currentDestination?.id
            val destinationId = item.itemId

            if (currentId != null && currentId != destinationId) {
                val currentIndex = menuOrder.indexOf(currentId)
                val destinationIndex = menuOrder.indexOf(destinationId)

                val builder = NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setRestoreState(true)

                if (destinationIndex > currentIndex) {
                    builder.setEnterAnim(R.anim.slide_in_right)
                        .setExitAnim(R.anim.slide_out_left)
                } else {
                    builder.setEnterAnim(R.anim.slide_in_left)
                        .setExitAnim(R.anim.slide_out_right)
                }

                navController.navigate(destinationId, null, builder.build())
            }
            true
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (bottomNav.selectedItemId != destination.id) {
                bottomNav.setOnItemSelectedListener(null)
                bottomNav.selectedItemId = destination.id

                bottomNav.setOnItemSelectedListener { item ->
                    val currentId = navController.currentDestination?.id
                    val destinationId = item.itemId
                    if (currentId != null && currentId != destinationId) {
                        val currentIndex = menuOrder.indexOf(currentId)
                        val destinationIndex = menuOrder.indexOf(destinationId)
                        val builder = NavOptions.Builder().setLaunchSingleTop(true).setRestoreState(true)
                        if (destinationIndex > currentIndex) {
                            builder.setEnterAnim(R.anim.slide_in_right).setExitAnim(R.anim.slide_out_left)
                        } else {
                            builder.setEnterAnim(R.anim.slide_in_left).setExitAnim(R.anim.slide_out_right)
                        }
                        navController.navigate(destinationId, null, builder.build())
                    }
                    true
                }
            }

            when (destination.id) {
                R.id.loginFragment, R.id.registerFragment, R.id.forgetPasswordFragment -> {
                    supportActionBar?.hide()
                    binding.toolbar.visibility = View.GONE
                    bottomNav.visibility = View.GONE
                }
                R.id.homeFragment, R.id.inventoryFragment, R.id.dropoffFragment, R.id.careGuideFragment -> {
                    supportActionBar?.hide()
                    binding.toolbar.visibility = View.GONE
                    bottomNav.visibility = View.VISIBLE
                }
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