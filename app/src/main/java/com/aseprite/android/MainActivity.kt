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
                R.id.nav_new_sprite -> {
                    val intent = Intent(this, NewSpriteActivity::class.java)
                    startActivityForResult(intent, 1001)
                    true
                }
                R.id.nav_open_sprite -> {
                    openFilePicker()
                    true
                }
                R.id.nav_settings -> {
                    Toast.makeText(this, "Settings - Coming Soon", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, getExternalFilesDir(null)?.toUri())
        }
        startActivityForResult(intent, 1002)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                1001 -> { // New sprite
                    val width = data.getIntExtra("width", 256)
                    val height = data.getIntExtra("height", 256)
                    val colorMode = data.getIntExtra("colorMode", 0)
                    createNewSprite(width, height, colorMode)
                }
                1002 -> { // Open sprite
                    val uri = data.data ?: return
                    val path = getPathFromUri(uri)
                    path?.let { openSprite(it) }
                }
            }
        }
    }

    private fun getPathFromUri(uri: Uri): String? {
        return try {
            contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
                pfd.statSize.toString()
            }
        } catch (e: Exception) {
            uri.path
        }
    }

    private fun createNewSprite(width: Int, height: Int, colorMode: Int) {
        val core = AsepriteCore.getInstance()
        val spritePtr = core.createSprite(width, height, colorMode)
        if (spritePtr > 0) {
            openEditor(spritePtr)
        } else {
            Toast.makeText(this, "Failed to create sprite", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openSprite(filePath: String) {
        val core = AsepriteCore.getInstance()
        val spritePtr = core.openSprite(filePath)
        if (spritePtr > 0) {
            openEditor(spritePtr)
        } else {
            Toast.makeText(this, "Failed to open sprite", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openEditor(spritePtr: Long) {
        val intent = Intent(this, EditorActivity::class.java).apply {
            putExtra("spritePtr", spritePtr)
        }
        startActivity(intent)
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
                val intent = Intent(this, NewSpriteActivity::class.java)
                startActivityForResult(intent, 1001)
                true
            }
            R.id.action_open -> {
                openFilePicker()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}