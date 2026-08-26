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
         * Initialize native library
         */
        fun initialize(): Boolean {
            return nativeInitialize()
        }

        /**
         * Shutdown native library
         */
        fun shutdown() {
            nativeShutdown()
            sInstance = null
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
}