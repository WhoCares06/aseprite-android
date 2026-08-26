package com.aseprite.android

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as androidx.navigation.fragment.NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_new_sprite, R.id.nav_open_sprite, R.id.nav_settings),
            drawerLayout
        )

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(navView, navController)

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    navController.navigate(R.id.mainFragment)
                    true
                }
                R.id.nav_new_sprite -> {
                    navController.navigate(R.id.action_mainFragment_to_newSpriteFragment)
                    true
                }
                R.id.nav_open_sprite -> {
                    navController.navigate(R.id.action_mainFragment_to_openSpriteFragment)
                    true
                }
                R.id.nav_settings -> {
                    navController.navigate(R.id.settingsFragment)
                    true
                }
                else -> false
            }
        }
        
        // Verify native library is initialized
        if (!AsepriteCore.isInitialized()) {
            Log.e("MainActivity", "Native library not initialized, attempting re-initialization")
            AsepriteCore.initialize()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_new -> {
                navController.navigate(R.id.action_mainFragment_to_newSpriteFragment)
                true
            }
            R.id.action_open -> {
                navController.navigate(R.id.action_mainFragment_to_openSpriteFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}