#include <jni.h>
#include <android/log.h>
#include <string>
#include <memory>

#include "aseprite_bridge.h"

#define LOG_TAG "AsepriteAndroid"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// Global bridge instance
static std::unique_ptr<AsepriteBridge> g_bridge = nullptr;

// Helper to get bridge instance
AsepriteBridge* getBridge() {
    if (!g_bridge) {
        g_bridge = std::make_unique<AsepriteBridge>();
    }
    return g_bridge.get();
}

// Safe pointer conversion - use union to avoid reinterpret_cast issues
static inline uintptr_t jlongToPtr(jlong value) {
    union { jlong j; uintptr_t p; } u;
    u.j = value;
    return u.p;
}

static inline jlong ptrToJlong(uintptr_t value) {
    union { jlong j; uintptr_t p; } u;
    u.p = value;
    return u.j;
}

// Create Android Bitmap using Bitmap.createBitmap via JNI
static jobject createBitmap(JNIEnv* env, int width, int height) {
    jclass bitmapClass = env->FindClass("android/graphics/Bitmap");
    if (!bitmapClass) {
        LOGE("Failed to find Bitmap class");
        return nullptr;
    }

    jmethodID createBitmapMethod = env->GetStaticMethodID(bitmapClass, "createBitmap", "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");
    if (!createBitmapMethod) {
        LOGE("Failed to find createBitmap method");
        return nullptr;
    }

    jclass configClass = env->FindClass("android/graphics/Bitmap$Config");
    if (!configClass) {
        LOGE("Failed to find Bitmap.Config class");
        return nullptr;
    }

    jfieldID argb8888Field = env->GetStaticFieldID(configClass, "ARGB_8888", "Landroid/graphics/Bitmap$Config;");
    if (!argb8888Field) {
        LOGE("Failed to find ARGB_8888 field");
        return nullptr;
    }

    jobject config = env->GetStaticObjectField(configClass, argb8888Field);
    if (!config) {
        LOGE("Failed to get ARGB_8888 config");
        return nullptr;
    }

    jobject bitmap = env->CallStaticObjectMethod(bitmapClass, createBitmapMethod, width, height, config);
    if (!bitmap) {
        LOGE("Failed to create bitmap");
        return nullptr;
    }

    return bitmap;
}

// Fill bitmap with checkerboard pattern (transparency indicator)
static void fillCheckerboard(JNIEnv* env, jobject bitmap, int width, int height) {
    // Use Bitmap.setPixels to fill the bitmap
    jclass bitmapClass = env->GetObjectClass(bitmap);
    jmethodID setPixelsMethod = env->GetMethodID(bitmapClass, "setPixels", "([IIIII)V");
    if (!setPixelsMethod) {
        LOGE("Failed to find setPixels method");
        return;
    }

    // Create int array with checkerboard pattern
    jintArray pixels = env->NewIntArray(width * height);
    if (!pixels) return;

    // Create buffer and fill
    jint* pixelData = env->GetIntArrayElements(pixels, nullptr);
    if (!pixelData) return;

    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            bool dark = ((x / 8) + (y / 8)) % 2 == 0;
            pixelData[y * width + x] = dark ? 0xFFCCCCCC : 0xFFFFFFFF;
        }
    }

    env->ReleaseIntArrayElements(pixels, pixelData, 0);
    env->CallVoidMethod(bitmap, setPixelsMethod, pixels, 0, width, 0, 0, width, height);
    env->DeleteLocalRef(pixels);
}

extern "C" {

// Initialize
JNIEXPORT jboolean JNICALL
Java_com_aseprite_android_AsepriteCore_nativeInitialize(JNIEnv* env, jclass clazz) {
    LOGD("JNI: nativeInitialize");
    AsepriteBridge* bridge = getBridge();
    if (!bridge->initialize()) {
        LOGE("Failed to initialize Aseprite bridge");
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

// Shutdown
JNIEXPORT void JNICALL
Java_com_aseprite_android_AsepriteCore_nativeShutdown(JNIEnv* env, jclass clazz) {
    LOGD("JNI: nativeShutdown");
    if (g_bridge) {
        g_bridge->shutdown();
        g_bridge.reset();
    }
}

// Create sprite
JNIEXPORT jlong JNICALL
Java_com_aseprite_android_AsepriteCore_nativeCreateSprite(JNIEnv* env, jclass clazz, jint width, jint height, jint colorMode) {
    LOGD("JNI: nativeCreateSprite %dx%d mode=%d", width, height, colorMode);
    AsepriteBridge* bridge = getBridge();
    return ptrToJlong(bridge->createSprite(static_cast<int>(width), static_cast<int>(height), static_cast<int>(colorMode)));
}

// Open sprite
JNIEXPORT jlong JNICALL
Java_com_aseprite_android_AsepriteCore_nativeOpenSprite(JNIEnv* env, jclass clazz, jstring filePath) {
    const char* path = env->GetStringUTFChars(filePath, nullptr);
    LOGD("JNI: nativeOpenSprite %s", path);
    AsepriteBridge* bridge = getBridge();
    jlong result = ptrToJlong(bridge->openSprite(path));
    env->ReleaseStringUTFChars(filePath, path);
    return result;
}

// Save sprite
JNIEXPORT jboolean JNICALL
Java_com_aseprite_android_AsepriteCore_nativeSaveSprite(JNIEnv* env, jclass clazz, jlong spritePtr, jstring filePath) {
    const char* path = env->GetStringUTFChars(filePath, nullptr);
    LOGD("JNI: nativeSaveSprite %s", path);
    AsepriteBridge* bridge = getBridge();
    jboolean result = bridge->saveSprite(jlongToPtr(spritePtr), path) ? JNI_TRUE : JNI_FALSE;
    env->ReleaseStringUTFChars(filePath, path);
    return result;
}

// Get width
JNIEXPORT jint JNICALL
Java_com_aseprite_android_AsepriteCore_nativeGetWidth(JNIEnv* env, jclass clazz, jlong spritePtr) {
    AsepriteBridge* bridge = getBridge();
    return static_cast<jint>(bridge->getWidth(jlongToPtr(spritePtr)));
}

// Get height
JNIEXPORT jint JNICALL
Java_com_aseprite_android_AsepriteCore_nativeGetHeight(JNIEnv* env, jclass clazz, jlong spritePtr) {
    AsepriteBridge* bridge = getBridge();
    return static_cast<jint>(bridge->getHeight(jlongToPtr(spritePtr)));
}

// Get frame count
JNIEXPORT jint JNICALL
Java_com_aseprite_android_AsepriteCore_nativeGetFrameCount(JNIEnv* env, jclass clazz, jlong spritePtr) {
    AsepriteBridge* bridge = getBridge();
    return static_cast<jint>(bridge->getFrameCount(jlongToPtr(spritePtr)));
}

// Get layer count
JNIEXPORT jint JNICALL
Java_com_aseprite_android_AsepriteCore_nativeGetLayerCount(JNIEnv* env, jclass clazz, jlong spritePtr) {
    AsepriteBridge* bridge = getBridge();
    return static_cast<jint>(bridge->getLayerCount(jlongToPtr(spritePtr)));
}

// Render frame - using Bitmap.createBitmap via JNI (no AndroidBitmap needed)
JNIEXPORT jobject JNICALL
Java_com_aseprite_android_AsepriteCore_nativeRenderFrame(JNIEnv* env, jclass clazz, jlong spritePtr, jint frameIndex) {
    LOGD("JNI: nativeRenderFrame %d", frameIndex);
    AsepriteBridge* bridge = getBridge();
    doc::Sprite* sprite = bridge->getSprite(jlongToPtr(spritePtr));
    if (!sprite) return nullptr;

    int width = bridge->getWidth(jlongToPtr(spritePtr));
    int height = bridge->getHeight(jlongToPtr(spritePtr));

    jobject bitmap = createBitmap(env, width, height);
    if (!bitmap) return nullptr;

    fillCheckerboard(env, bitmap, width, height);
    return bitmap;
}

// Create layer
JNIEXPORT jlong JNICALL
Java_com_aseprite_android_AsepriteCore_nativeCreateLayer(JNIEnv* env, jclass clazz, jlong spritePtr, jstring name) {
    const char* layerName = env->GetStringUTFChars(name, nullptr);
    LOGD("JNI: nativeCreateLayer %s", layerName);
    AsepriteBridge* bridge = getBridge();
    jlong result = ptrToJlong(bridge->createLayer(jlongToPtr(spritePtr), layerName));
    env->ReleaseStringUTFChars(name, layerName);
    return result;
}

// Delete layer
JNIEXPORT void JNICALL
Java_com_aseprite_android_AsepriteCore_nativeDeleteLayer(JNIEnv* env, jclass clazz, jlong spritePtr, jint layerIndex) {
    LOGD("JNI: nativeDeleteLayer %d", layerIndex);
    AsepriteBridge* bridge = getBridge();
    bridge->deleteLayer(jlongToPtr(spritePtr), static_cast<int>(layerIndex));
}

// Get pixel
JNIEXPORT jint JNICALL
Java_com_aseprite_android_AsepriteCore_nativeGetPixel(JNIEnv* env, jclass clazz, jlong spritePtr, jint frame, jint layer, jint x, jint y) {
    AsepriteBridge* bridge = getBridge();
    return static_cast<jint>(bridge->getPixel(jlongToPtr(spritePtr), static_cast<int>(frame), static_cast<int>(layer), static_cast<int>(x), static_cast<int>(y)));
}

// Set pixel
JNIEXPORT void JNICALL
Java_com_aseprite_android_AsepriteCore_nativeSetPixel(JNIEnv* env, jclass clazz, jlong spritePtr, jint frame, jint layer, jint x, jint y, jint color) {
    AsepriteBridge* bridge = getBridge();
    bridge->setPixel(jlongToPtr(spritePtr), static_cast<int>(frame), static_cast<int>(layer), static_cast<int>(x), static_cast<int>(y), static_cast<int>(color));
}

// Undo
JNIEXPORT void JNICALL
Java_com_aseprite_android_AsepriteCore_nativeUndo(JNIEnv* env, jclass clazz, jlong spritePtr) {
    LOGD("JNI: nativeUndo");
    AsepriteBridge* bridge = getBridge();
    bridge->undo(jlongToPtr(spritePtr));
}

// Redo
JNIEXPORT void JNICALL
Java_com_aseprite_android_AsepriteCore_nativeRedo(JNIEnv* env, jclass clazz, jlong spritePtr) {
    LOGD("JNI: nativeRedo");
    AsepriteBridge* bridge = getBridge();
    bridge->redo(jlongToPtr(spritePtr));
}

// Can undo
JNIEXPORT jboolean JNICALL
Java_com_aseprite_android_AsepriteCore_nativeCanUndo(JNIEnv* env, jclass clazz, jlong spritePtr) {
    AsepriteBridge* bridge = getBridge();
    return bridge->canUndo(jlongToPtr(spritePtr)) ? JNI_TRUE : JNI_FALSE;
}

// Can redo
JNIEXPORT jboolean JNICALL
Java_com_aseprite_android_AsepriteCore_nativeCanRedo(JNIEnv* env, jclass clazz, jlong spritePtr) {
    AsepriteBridge* bridge = getBridge();
    return bridge->canRedo(jlongToPtr(spritePtr)) ? JNI_TRUE : JNI_FALSE;
}

// Export PNG
JNIEXPORT jboolean JNICALL
Java_com_aseprite_android_AsepriteCore_nativeExportPNG(JNIEnv* env, jclass clazz, jlong spritePtr, jstring filePath) {
    const char* path = env->GetStringUTFChars(filePath, nullptr);
    LOGD("JNI: nativeExportPNG %s", path);
    AsepriteBridge* bridge = getBridge();
    jboolean result = bridge->exportPNG(jlongToPtr(spritePtr), path) ? JNI_TRUE : JNI_FALSE;
    env->ReleaseStringUTFChars(filePath, path);
    return result;
}

// Export GIF
JNIEXPORT jboolean JNICALL
Java_com_aseprite_android_AsepriteCore_nativeExportGIF(JNIEnv* env, jclass clazz, jlong spritePtr, jstring filePath) {
    const char* path = env->GetStringUTFChars(filePath, nullptr);
    LOGD("JNI: nativeExportGIF %s", path);
    AsepriteBridge* bridge = getBridge();
    jboolean result = bridge->exportGIF(jlongToPtr(spritePtr), path) ? JNI_TRUE : JNI_FALSE;
    env->ReleaseStringUTFChars(filePath, path);
    return result;
}

// Export Sprite Sheet
JNIEXPORT jboolean JNICALL
Java_com_aseprite_android_AsepriteCore_nativeExportSpriteSheet(JNIEnv* env, jclass clazz, jlong spritePtr, jstring filePath, jint columns) {
    const char* path = env->GetStringUTFChars(filePath, nullptr);
    LOGD("JNI: nativeExportSpriteSheet %s cols=%d", path, columns);
    AsepriteBridge* bridge = getBridge();
    jboolean result = bridge->exportSpriteSheet(jlongToPtr(spritePtr), path, static_cast<int>(columns)) ? JNI_TRUE : JNI_FALSE;
    env->ReleaseStringUTFChars(filePath, path);
    return result;
}

} // extern "C"