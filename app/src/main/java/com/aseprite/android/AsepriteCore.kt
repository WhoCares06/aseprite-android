package com.aseprite.android

import android.content.Context
import android.graphics.Bitmap
import android.util.Log

/**
 * Kotlin wrapper for Aseprite native core
 * Provides type-safe interface to JNI functions
 */
class AsepriteCore private constructor() {
    companion object {
        @Suppress("UNUSED_PARAMETER")
        private var initialized = false
        
        fun initialize(): Boolean {
            if (initialized) return true
            initialized = nativeInitialize()
            return initialized
        }
        
        fun shutdown() {
            if (!initialized) return
            nativeShutdown()
            initialized = false
        }
        
        fun isInitialized() = initialized
    }
    
    // Sprite management
    data class SpriteInfo(
        val width: Int,
        val height: Int,
        val frameCount: Int,
        val layerCount: Int,
        val colorMode: Int
    )
    
    fun createSprite(width: Int, height: Int, colorMode: Int = ColorMode.RGB): Long {
        return nativeCreateSprite(width, height, colorMode)
    }
    
    fun openSprite(filePath: String): Long {
        return nativeOpenSprite(filePath)
    }
    
    fun saveSprite(spritePtr: Long, filePath: String): Boolean {
        return nativeSaveSprite(spritePtr, filePath)
    }
    
    fun getSpriteInfo(spritePtr: Long): SpriteInfo? {
        if (spritePtr == 0L) return null
        return SpriteInfo(
            width = nativeGetWidth(spritePtr),
            height = nativeGetHeight(spritePtr),
            frameCount = nativeGetFrameCount(spritePtr),
            layerCount = nativeGetLayerCount(spritePtr),
            colorMode = 0 // TODO: get from sprite
        )
    }
    
    // Frame rendering
    fun renderFrame(spritePtr: Long, frameIndex: Int): Bitmap? {
        return nativeRenderFrame(spritePtr, frameIndex)
    }
    
    // Layer operations
    fun createLayer(spritePtr: Long, name: String): Long {
        return nativeCreateLayer(spritePtr, name)
    }
    
    fun deleteLayer(spritePtr: Long, layerIndex: Int) {
        nativeDeleteLayer(spritePtr, layerIndex)
    }
    
    // Pixel operations
    fun getPixel(spritePtr: Long, frame: Int, layer: Int, x: Int, y: Int): Int {
        return nativeGetPixel(spritePtr, frame, layer, x, y)
    }
    
    fun setPixel(spritePtr: Long, frame: Int, layer: Int, x: Int, y: Int, color: Int) {
        nativeSetPixel(spritePtr, frame, layer, x, y, color)
    }
    
    // Undo/Redo
    fun undo(spritePtr: Long) {
        nativeUndo(spritePtr)
    }
    
    fun redo(spritePtr: Long) {
        nativeRedo(spritePtr)
    }
    
    fun canUndo(spritePtr: Long): Boolean {
        return nativeCanUndo(spritePtr)
    }
    
    fun canRedo(spritePtr: Long): Boolean {
        return nativeCanRedo(spritePtr)
    }
    
    // Export
    fun exportPNG(spritePtr: Long, filePath: String): Boolean {
        return nativeExportPNG(spritePtr, filePath)
    }
    
    fun exportGIF(spritePtr: Long, filePath: String): Boolean {
        return nativeExportGIF(spritePtr, filePath)
    }
    
    fun exportSpriteSheet(spritePtr: Long, filePath: String, columns: Int): Boolean {
        return nativeExportSpriteSheet(spritePtr, filePath, columns)
    }
    
    // Color modes
    object ColorMode {
        const val RGB = 0
        const val GRAYSCALE = 1
        const val INDEXED = 2
    }
    
    // Native methods - match JNI export names (Java_com_aseprite_android_AsepriteCore_<method>)
    // These MUST match the JNI exports exactly
    @Suppress("UNUSED_PARAMETER")
    external fun initialize(): Boolean
    @Suppress("UNUSED_PARAMETER")
    external fun shutdown()
    @Suppress("UNUSED_PARAMETER")
    external fun createSprite(width: Int, height: Int, colorMode: Int): Long
    @Suppress("UNUSED_PARAMETER")
    external fun openSprite(filePath: String): Long
    @Suppress("UNUSED_PARAMETER")
    external fun saveSprite(spritePtr: Long, filePath: String): Boolean
    @Suppress("UNUSED_PARAMETER")
    external fun getWidth(spritePtr: Long): Int
    @Suppress("UNUSED_PARAMETER")
    external fun getHeight(spritePtr: Long): Int
    @Suppress("UNUSED_PARAMETER")
    external fun renderFrame(spritePtr: Long, frameIndex: Int): Bitmap?
    @Suppress("UNUSED_PARAMETER")
    external fun getFrameCount(spritePtr: Long): Int
    @Suppress("UNUSED_PARAMETER")
    external fun getLayerCount(spritePtr: Long): Int
    @Suppress("UNUSED_PARAMETER")
    external fun createLayer(spritePtr: Long, name: String): Long
    @Suppress("UNUSED_PARAMETER")
    external fun deleteLayer(spritePtr: Long, layerIndex: Int)
    @Suppress("UNUSED_PARAMETER")
    external fun getPixel(spritePtr: Long, frame: Int, layer: Int, x: Int, y: Int): Int
    @Suppress("UNUSED_PARAMETER")
    external fun setPixel(spritePtr: Long, frame: Int, layer: Int, x: Int, y: Int, color: Int)
    @Suppress("UNUSED_PARAMETER")
    external fun undo(spritePtr: Long)
    @Suppress("UNUSED_PARAMETER")
    external fun redo(spritePtr: Long)
    @Suppress("UNUSED_PARAMETER")
    external fun canUndo(spritePtr: Long): Boolean
    @Suppress("UNUSED_PARAMETER")
    external fun canRedo(spritePtr: Long): Boolean
    @Suppress("UNUSED_PARAMETER")
    external fun exportPNG(spritePtr: Long, filePath: String): Boolean
    @Suppress("UNUSED_PARAMETER")
    external fun exportGIF(spritePtr: Long, filePath: String): Boolean
    @Suppress("UNUSED_PARAMETER")
    external fun exportSpriteSheet(spritePtr: Long, filePath: String, columns: Int): Boolean
    
    // Load native library
    init {
        System.loadLibrary("aseprite")
    }
}