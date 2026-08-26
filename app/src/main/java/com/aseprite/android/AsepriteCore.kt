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
            initialized = initializeNative()
            return initialized
        }
        
        fun shutdown() {
            if (!initialized) return
            shutdownNative()
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
        return createSpriteNative(width, height, colorMode)
    }
    
    fun openSprite(filePath: String): Long {
        return openSpriteNative(filePath)
    }
    
    fun saveSprite(spritePtr: Long, filePath: String): Boolean {
        return saveSpriteNative(spritePtr, filePath)
    }
    
    fun getSpriteInfo(spritePtr: Long): SpriteInfo? {
        if (spritePtr == 0L) return null
        return SpriteInfo(
            width = getWidthNative(spritePtr),
            height = getHeightNative(spritePtr),
            frameCount = getFrameCountNative(spritePtr),
            layerCount = getLayerCountNative(spritePtr),
            colorMode = 0 // TODO: get from sprite
        )
    }
    
    // Frame rendering
    fun renderFrame(spritePtr: Long, frameIndex: Int): Bitmap? {
        return renderFrameNative(spritePtr, frameIndex)
    }
    
    // Layer operations
    fun createLayer(spritePtr: Long, name: String): Long {
        return createLayerNative(spritePtr, name)
    }
    
    fun deleteLayer(spritePtr: Long, layerIndex: Int) {
        deleteLayerNative(spritePtr, layerIndex)
    }
    
    // Pixel operations
    fun getPixel(spritePtr: Long, frame: Int, layer: Int, x: Int, y: Int): Int {
        return getPixelNative(spritePtr, frame, layer, x, y)
    }
    
    fun setPixel(spritePtr: Long, frame: Int, layer: Int, x: Int, y: Int, color: Int) {
        setPixelNative(spritePtr, frame, layer, x, y, color)
    }
    
    // Undo/Redo
    fun undo(spritePtr: Long) {
        undoNative(spritePtr)
    }
    
    fun redo(spritePtr: Long) {
        redoNative(spritePtr)
    }
    
    fun canUndo(spritePtr: Long): Boolean {
        return canUndoNative(spritePtr)
    }
    
    fun canRedo(spritePtr: Long): Boolean {
        return canRedoNative(spritePtr)
    }
    
    // Export
    fun exportPNG(spritePtr: Long, filePath: String): Boolean {
        return exportPNGNative(spritePtr, filePath)
    }
    
    fun exportGIF(spritePtr: Long, filePath: String): Boolean {
        return exportGIFNative(spritePtr, filePath)
    }
    
    fun exportSpriteSheet(spritePtr: Long, filePath: String, columns: Int): Boolean {
        return exportSpriteSheetNative(spritePtr, filePath, columns)
    }
    
    // Color modes
    object ColorMode {
        const val RGB = 0
        const val GRAYSCALE = 1
        const val INDEXED = 2
    }
    
    // Native methods - match JNI export names (Java_com_aseprite_android_AsepriteCore_<method>)
    external fun initializeNative(): Boolean
    external fun shutdownNative()
    external fun createSpriteNative(width: Int, height: Int, colorMode: Int): Long
    external fun openSpriteNative(filePath: String): Long
    external fun saveSpriteNative(spritePtr: Long, filePath: String): Boolean
    external fun getWidthNative(spritePtr: Long): Int
    external fun getHeightNative(spritePtr: Long): Int
    external fun renderFrameNative(spritePtr: Long, frameIndex: Int): Bitmap?
    external fun getFrameCountNative(spritePtr: Long): Int
    external fun getLayerCountNative(spritePtr: Long): Int
    external fun createLayerNative(spritePtr: Long, name: String): Long
    external fun deleteLayerNative(spritePtr: Long, layerIndex: Int)
    external fun getPixelNative(spritePtr: Long, frame: Int, layer: Int, x: Int, y: Int): Int
    external fun setPixelNative(spritePtr: Long, frame: Int, layer: Int, x: Int, y: Int, color: Int)
    external fun undoNative(spritePtr: Long)
    external fun redoNative(spritePtr: Long)
    external fun canUndoNative(spritePtr: Long): Boolean
    external fun canRedoNative(spritePtr: Long): Boolean
    external fun exportPNGNative(spritePtr: Long, filePath: String): Boolean
    external fun exportGIFNative(spritePtr: Long, filePath: String): Boolean
    external fun exportSpriteSheetNative(spritePtr: Long, filePath: String, columns: Int): Boolean
    
    // Load native library
    init {
        System.loadLibrary("aseprite")
    }
}