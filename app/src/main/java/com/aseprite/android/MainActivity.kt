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
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import com.aseprite.android.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentSpritePtr: Long = 0
    private var currentFilePath: String? = null
    private lateinit var navController: androidx.navigation.NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val OPEN_FILE_REQUEST = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // Initialize Aseprite core
        if (!AsepriteCore.initialize()) {
            Toast.makeText(this, "Failed to initialize Aseprite core", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(setOf(R.id.mainFragment), binding.drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
    }

    fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/png", "image/gif", "image/aseprite"))
        }
        startActivityForResult(intent, OPEN_FILE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK || data == null) return

        val uri = data.data ?: return

        when (requestCode) {
            OPEN_FILE_REQUEST -> {
                takePersistableUriPermission(uri)
                loadSpriteFromUri(uri)
            }
        }
    }

    private fun takePersistableUriPermission(uri: Uri) {
        val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        contentResolver.takePersistableUriPermission(uri, flags)
    }

    private fun loadSpriteFromUri(uri: Uri) {
        try {
            val filePath = getPathFromUri(uri)
            if (filePath != null) {
                currentSpritePtr = AsepriteCore().openSprite(filePath)
                if (currentSpritePtr != 0L) {
                    currentFilePath = filePath
                    Toast.makeText(this, "Sprite loaded", Toast.LENGTH_SHORT).show()
                    openEditor()
                } else {
                    Toast.makeText(this, "Failed to load sprite", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun getPathFromUri(uri: Uri): String? {
        // For simplicity, copy to cache dir
        try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val fileName = DocumentsContract.getDocumentId(uri).split(":").last()
            val cacheFile = File(cacheDir, fileName)
            val outputStream = FileOutputStream(cacheFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            return cacheFile.absolutePath
        } catch (e: Exception) {
            return null
        }
    }

    private fun openEditor() {
        val action = MainFragmentDirections.actionMainFragmentToEditorFragment(currentSpritePtr)
        navController.navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_about -> {
                Toast.makeText(this, "Aseprite Android v1.0", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_settings -> {
                Toast.makeText(this, "Settings coming soon", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        AsepriteCore.shutdown()
    }
}