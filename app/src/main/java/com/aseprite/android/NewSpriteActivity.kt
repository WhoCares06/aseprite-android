package com.aseprite.android

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aseprite.android.databinding.ActivityNewSpriteBinding

class NewSpriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewSpriteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewSpriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Default values
        binding.etWidth.setText("256")
        binding.etHeight.setText("256")

        binding.btnCreate.setOnClickListener { createSprite() }
        binding.btnCancel.setOnClickListener { finish() }
    }

    private fun createSprite() {
        val width = binding.etWidth.text.toString().toIntOrNull() ?: 256
        val height = binding.etHeight.text.toString().toIntOrNull() ?: 256
        val colorMode = when (binding.spinnerColorMode.selectedItemPosition) {
            0 -> AsepriteCore.ColorMode.RGB
            1 -> AsepriteCore.ColorMode.GRAYSCALE
            2 -> AsepriteCore.ColorMode.INDEXED
            else -> AsepriteCore.ColorMode.RGB
        }

        if (width < 1 || width > 4096 || height < 1 || height > 4096) {
            Toast.makeText(this, "Invalid dimensions (1-4096)", Toast.LENGTH_SHORT).show()
            return
        }

        val spritePtr = AsepriteCore().createSprite(width, height, colorMode)
        if (spritePtr != 0L) {
            val intent = Intent().putExtra("sprite_ptr", spritePtr)
            setResult(Activity.RESULT_OK, intent)
            finish()
        } else {
            Toast.makeText(this, "Failed to create sprite", Toast.LENGTH_SHORT).show()
        }
    }
}