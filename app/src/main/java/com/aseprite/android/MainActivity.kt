package com.aseprite.android

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private var currentSprite: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContentView(R.layout.activity_main)
        
        // Initialize Aseprite core
        lifecycleScope.launch {
            val success = AsepriteCore.initialize()
            if (!success) {
                Toast.makeText(this@MainActivity, "Failed to initialize Aseprite core", Toast.LENGTH_LONG).show()
            }
        }
        
        setupToolbar()
        setupNavigation()
        setupDrawer()
    }
    
    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
    }
    
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_new, R.id.nav_open, R.id.nav_recent),
            (findViewById(R.id.drawer_layout) as DrawerLayout)
        )
        
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)
        
        val navView = findViewById<NavigationView>(R.id.nav_view)
        NavigationUI.setupWithNavController(navView, navController)
    }
    
    private fun setupDrawer() {
        val navView = findViewById<NavigationView>(R.id.nav_view)
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_new -> createNewSprite()
                R.id.nav_open -> openSprite()
                R.id.nav_recent -> navController.navigate(R.id.nav_recent)
                R.id.nav_settings -> navController.navigate(R.id.nav_settings)
                R.id.nav_about -> showAboutDialog()
            }
            (findViewById(R.id.drawer_layout) as DrawerLayout).closeDrawers()
            true
        }
    }
    
    private fun createNewSprite() {
        // TODO: Show dialog for new sprite dimensions
        val intent = Intent(this, NewSpriteActivity::class.java)
        startActivityForResult(intent, REQUEST_NEW_SPRITE)
    }
    
    private fun openSprite() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(
                "image/png", "image/gif", "image/aseprite", "application/octet-stream"
            ))
        }
        startActivityForResult(intent, REQUEST_OPEN_SPRITE)
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                REQUEST_NEW_SPRITE -> {
                    data.getLongExtra("sprite_ptr", 0).let { ptr ->
                        if (ptr != 0L) {
                            currentSprite = ptr
                            navController.navigate(R.id.nav_editor)
                        }
                    }
                }
                REQUEST_OPEN_SPRITE -> {
                    data.data?.let { uri ->
                        openSpriteFromUri(uri)
                    }
                }
            }
        }
    }
    
    private fun openSpriteFromUri(uri: Uri) {
        lifecycleScope.launch {
            // Copy to local file if needed
            val filePath = copyUriToFile(uri) ?: return@launch
            val ptr = AsepriteCore.openSprite(filePath)
            if (ptr != 0L) {
                currentSprite = ptr
                navController.navigate(R.id.nav_editor)
            } else {
                Toast.makeText(this@MainActivity, "Failed to open sprite", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun copyUriToFile(uri: Uri): String? {
        // TODO: Copy content URI to local cache file
        // For now, return null to indicate not implemented
        return null
    }
    
    private fun showAboutDialog() {
        // TODO: Show about dialog
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_undo -> {
                if (currentSprite != 0L) AsepriteCore.undo(currentSprite)
                true
            }
            R.id.action_redo -> {
                if (currentSprite != 0L) AsepriteCore.redo(currentSprite)
                true
            }
            R.id.action_export_png -> {
                if (currentSprite != 0L) exportCurrentSprite("png")
                true
            }
            R.id.action_export_gif -> {
                if (currentSprite != 0L) exportCurrentSprite("gif")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun exportCurrentSprite(format: String) {
        // TODO: Show save dialog and export
        Toast.makeText(this, "Export $format not implemented yet", Toast.LENGTH_SHORT).show()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        if (currentSprite != 0L) {
            // Don't shutdown core as other activities might need it
            // AsepriteCore.shutdown()
        }
    }
    
    companion object {
        private const val REQUEST_NEW_SPRITE = 1001
        private const val REQUEST_OPEN_SPRITE = 1002
    }
}