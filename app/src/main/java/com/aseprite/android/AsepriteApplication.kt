package com.aseprite.android

import android.app.Application
import android.util.Log

class AsepriteApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d("AsepriteApp", "=== AsepriteApplication.onCreate() START ===")
        
        // Initialize AsepriteCore with comprehensive error handling
        try {
            Log.d("AsepriteApp", "Calling AsepriteCore.initialize()...")
            val initialized = AsepriteCore.initialize()
            Log.d("AsepriteApp", "AsepriteCore.initialize() returned: $initialized")
            if (!initialized) {
                Log.e("AsepriteApp", "AsepriteCore.initialize() FAILED - returning false")
            }
        } catch (e: UnsatisfiedLinkError) {
            Log.e("AsepriteApp", "UnsatisfiedLinkError loading native library: ${e.message}", e)
            Log.e("AsepriteApp", "Library path: ${System.getProperty("java.library.path")}")
        } catch (e: Exception) {
            Log.e("AsepriteApp", "Exception during initialization: ${e.message}", e)
        }
        
        Log.d("AsepriteApp", "=== AsepriteApplication.onCreate() END ===")
    }
}