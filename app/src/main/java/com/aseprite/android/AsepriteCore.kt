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
    external fun nativeInitialize(): Boolean
    external fun nativeShutdown()
    external fun nativeCreateSprite(width: Int, height: Int, colorMode: Int): Long
    external fun nativeOpenSprite(filePath: String): Long
    external fun nativeSaveSprite(spritePtr: Long, filePath: String): Boolean
    external fun nativeGetWidth(spritePtr: Long): Int
    external fun nativeGetHeight(spritePtr: Long): Int
    external fun nativeRenderFrame(spritePtr: Long, frameIndex: Int): Bitmap?
    external fun nativeGetFrameCount(spritePtr: Long): Int
    external fun nativeGetLayerCount(spritePtr: Long): Int
    external fun nativeCreateLayer(spritePtr: Long, name: String): Long
    external fun nativeDeleteLayer(spritePtr: Long, layerIndex: Int)
    external fun nativeGetPixel(spritePtr: Long, frame: Int, layer: Int, x: Int, y: Int): Int
    external fun nativeSetPixel(spritePtr: Long, frame: Int, layer: Int, x: Int, y: Int, color: Int)
    external fun nativeUndo(spritePtr: Long)
    external fun nativeRedo(spritePtr: Long)
    external fun nativeCanUndo(spritePtr: Long): Boolean
    external fun nativeCanRedo(spritePtr: Long): Boolean
    external fun nativeExportPNG(spritePtr: Long, filePath: String): Boolean
    external fun nativeExportGIF(spritePtr: Long, filePath: String): Boolean
    external fun nativeExportSpriteSheet(spritePtr: Long, filePath: String, columns: Int): Boolean
    
    // Load native library
    init {
        System.loadLibrary("aseprite")
    }
}