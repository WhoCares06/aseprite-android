package com.aseprite.android

import android.content.Context
import android.graphics.Bitmap
import android.util.Log

/**
 * Kotlin wrapper for Aseprite native core
 * Provides type-safe interface to JNI methods
 */
class AsepriteCore private constructor() {

    companion object {
        private const val TAG = "AsepriteCore"
        private var sInstance: AsepriteCore? = null
        private var sInitialized = false

        /**
         * Get singleton instance
         */
        fun getInstance(): AsepriteCore {
            if (sInstance == null) {
                synchronized(this) {
                    if (sInstance == null) {
                        sInstance = AsepriteCore()
                    }
                }
            }
            return sInstance!!
        }

        /**
         * Check if native library is initialized
         */
        fun isInitialized(): Boolean {
            return sInitialized
        }

        /**
         * Initialize native library
         */
        fun initialize(): Boolean {
            try {
                val result = nativeInitialize()
                sInitialized = result
                if (result) {
                    Log.d(TAG, "Native library initialized successfully")
                } else {
                    Log.e(TAG, "Native library initialization failed")
                }
                return result
            } catch (e: UnsatisfiedLinkError) {
                Log.e(TAG, "Native library not found: ${e.message}", e)
                sInitialized = false
                return false
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing native library: ${e.message}", e)
                sInitialized = false
                return false
            }
        }

        /**
         * Shutdown native library
         */
        fun shutdown() {
            nativeShutdown()
            sInstance = null
            sInitialized = false
        }

        @JvmStatic
        external fun nativeInitialize(): Boolean

        @JvmStatic
        external fun nativeShutdown()

        // Sprite creation/loading
        @JvmStatic
        external fun nativeCreateSprite(width: Int, height: Int, colorMode: Int): Long

        @JvmStatic
        external fun nativeOpenSprite(filePath: String): Long

        @JvmStatic
        external fun nativeSaveSprite(spritePtr: Long, filePath: String): Boolean

        // Sprite properties
        @JvmStatic
        external fun nativeGetWidth(spritePtr: Long): Int

        @JvmStatic
        external fun nativeGetHeight(spritePtr: Long): Int

        @JvmStatic
        external fun nativeGetFrameCount(spritePtr: Long): Int

        @JvmStatic
        external fun nativeGetLayerCount(spritePtr: Long): Int

        // Frame rendering
        @JvmStatic
        external fun nativeRenderFrame(spritePtr: Long, frameIndex: Int): Bitmap?

        // Layer operations
        @JvmStatic
        external fun nativeCreateLayer(spritePtr: Long, name: String): Long

        @JvmStatic
        external fun nativeDeleteLayer(spritePtr: Long, layerIndex: Int)

        // Pixel operations
        @JvmStatic
        external fun nativeGetPixel(spritePtr: Long, frame: Int, layer: Int, x: Int, y: Int): Int

        @JvmStatic
        external fun nativeSetPixel(spritePtr: Long, frame: Int, layer: Int, x: Int, y: Int, color: Int)

        // Undo/Redo
        @JvmStatic
        external fun nativeUndo(spritePtr: Long)

        @JvmStatic
        external fun nativeRedo(spritePtr: Long)

        @JvmStatic
        external fun nativeCanUndo(spritePtr: Long): Boolean

        @JvmStatic
        external fun nativeCanRedo(spritePtr: Long): Boolean

        // Export
        @JvmStatic
        external fun nativeExportPNG(spritePtr: Long, filePath: String): Boolean

        @JvmStatic
        external fun nativeExportGIF(spritePtr: Long, filePath: String): Boolean

        @JvmStatic
        external fun nativeExportSpriteSheet(spritePtr: Long, filePath: String, columns: Int): Boolean

        init {
            System.loadLibrary("aseprite")
        }
    }

    // Instance methods that wrap the static JNI calls
    fun createSprite(width: Int, height: Int, colorMode: Int): Long {
        return nativeCreateSprite(width, height, colorMode)
    }

    fun openSprite(filePath: String): Long {
        return nativeOpenSprite(filePath)
    }

    fun saveSprite(spritePtr: Long, filePath: String): Boolean {
        return nativeSaveSprite(spritePtr, filePath)
    }

    fun getWidth(spritePtr: Long): Int {
        return nativeGetWidth(spritePtr)
    }

    fun getHeight(spritePtr: Long): Int {
        return nativeGetHeight(spritePtr)
    }

    fun getFrameCount(spritePtr: Long): Int {
        return nativeGetFrameCount(spritePtr)
    }

    fun getLayerCount(spritePtr: Long): Int {
        return nativeGetLayerCount(spritePtr)
    }

    fun renderFrame(spritePtr: Long, frameIndex: Int): Bitmap? {
        return nativeRenderFrame(spritePtr, frameIndex)
    }

    fun createLayer(spritePtr: Long, name: String): Long {
        return nativeCreateLayer(spritePtr, name)
    }

    fun deleteLayer(spritePtr: Long, layerIndex: Int) {
        nativeDeleteLayer(spritePtr, layerIndex)
    }

    fun getPixel(spritePtr: Long, frame: Int, layer: Int, x: Int, y: Int): Int {
        return nativeGetPixel(spritePtr, frame, layer, x, y)
    }

    fun setPixel(spritePtr: Long, frame: Int, layer: Int, x: Int, y: Int, color: Int) {
        nativeSetPixel(spritePtr, frame, layer, x, y, color)
    }

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

    fun exportPNG(spritePtr: Long, filePath: String): Boolean {
        return nativeExportPNG(spritePtr, filePath)
    }

    fun exportGIF(spritePtr: Long, filePath: String): Boolean {
        return nativeExportGIF(spritePtr, filePath)
    }

    fun exportSpriteSheet(spritePtr: Long, filePath: String, columns: Int): Boolean {
        return nativeExportSpriteSheet(spritePtr, filePath, columns)
    }
}