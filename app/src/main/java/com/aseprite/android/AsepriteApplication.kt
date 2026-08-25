package com.aseprite.android

import android.app.Application
import android.util.Log

class AsepriteApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d("AsepriteApplication", "Application created")
    }
}