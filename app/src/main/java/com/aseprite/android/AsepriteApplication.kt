package com.aseprite.android

import android.app.Application
import android.util.Log

class AsepriteApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d("AsepriteApplication", "Application created - initializing native library")
        
        // Initialize native library early with error handling
        try {
            val success = AsepriteCore.initialize()
            if (success) {
                Log.d("AsepriteApplication", "Native library initialized successfully")
            } else {
                Log.e("AsepriteApplication", "Failed to initialize native library")
            }
        } catch (e: UnsatisfiedLinkError) {
            Log.e("AsepriteApplication", "Native library not found: ${e.message}", e)
        } catch (e: Exception) {
            Log.e("AsepriteApplication", "Error initializing native library: ${e.message}", e)
        }
    }
}