package com.aseprite.android

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class NewSpriteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_sprite)
        
        val widthInput = findViewById<EditText>(R.id.width_input)
        val heightInput = findViewById<EditText>(R.id.height_input)
        val colorModeGroup = findViewById<RadioGroup>(R.id.color_mode_group)
        val createButton = findViewById<Button>(R.id.create_button)
        val cancelButton = findViewById<Button>(R.id.cancel_button)
        
        // Set defaults
        widthInput.setText("320")
        heightInput.setText("180")
        
        createButton.setOnClickListener {
            val width = widthInput.text.toString().toIntOrNull() ?: 320
            val height = heightInput.text.toString().toIntOrNull() ?: 180
            
            if (width < 1 || width > 8192 || height < 1 || height > 8192) {
                Toast.makeText(this, "Width/Height must be between 1 and 8192", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val colorMode = when (colorModeGroup.checkedRadioButtonId) {
                R.id.radio_rgb -> AsepriteCore.ColorMode.RGB
                R.id.radio_grayscale -> AsepriteCore.ColorMode.GRAYSCALE
                R.id.radio_indexed -> AsepriteCore.ColorMode.INDEXED
                else -> AsepriteCore.ColorMode.RGB
            }
            
            val spritePtr = AsepriteCore.createSprite(width, height, colorMode)
            if (spritePtr != 0L) {
                val result = Intent().putExtra("sprite_ptr", spritePtr)
                setResult(Activity.RESULT_OK, result)
                finish()
            } else {
                Toast.makeText(this, "Failed to create sprite", Toast.LENGTH_SHORT).show()
            }
        }
        
        cancelButton.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}