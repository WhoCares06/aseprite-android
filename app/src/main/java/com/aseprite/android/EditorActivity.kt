package com.aseprite.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class EditorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)
        
        if (savedInstanceState == null) {
            val spritePtr = intent.getLongExtra("sprite_ptr", 0)
            if (spritePtr != 0L) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.editor_container, EditorFragment().apply {
                        arguments = Bundle().apply { putLong("sprite_ptr", spritePtr) }
                    })
                    .commit()
            } else {
                finish()
            }
        }
    }
}