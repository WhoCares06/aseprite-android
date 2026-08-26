package com.aseprite.android

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class NewSpriteActivity : AppCompatActivity() {
    private lateinit var etWidth: EditText
    private lateinit var etHeight: EditText
    private lateinit var rgColorMode: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_sprite)

        etWidth = findViewById(R.id.et_width)
        etHeight = findViewById(R.id.et_height)
        rgColorMode = findViewById(R.id.rg_color_mode)

        findViewById<Button>(R.id.btn_create).setOnClickListener { createSprite() }
        findViewById<Button>(R.id.btn_cancel).setOnClickListener { finish() }
    }

    private fun createSprite() {
        val width = etWidth.text.toString().toIntOrNull() ?: 256
        val height = etHeight.text.toString().toIntOrNull() ?: 256

        if (width !in 1..4096 || height !in 1..4096) {
            Toast.makeText(this, "Width/Height must be 1-4096", Toast.LENGTH_SHORT).show()
            return
        }

        val colorMode = when (rgColorMode.checkedRadioButtonId) {
            R.id.rb_rgba -> 0 // RGBA
            R.id.rb_grayscale -> 1 // Grayscale
            R.id.rb_indexed -> 2 // Indexed
            else -> 0
        }

        val result = Intent().apply {
            putExtra("width", width)
            putExtra("height", height)
            putExtra("colorMode", colorMode)
        }
        setResult(Activity.RESULT_OK, result)
        finish()
    }
}