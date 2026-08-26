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

extern "C" {

// Initialize
JNIEXPORT jboolean JNICALL
Java_com_aseprite_android_AsepriteCore_initialize(JNIEnv* env, jobject thiz) {
    LOGD("JNI: initialize");
    AsepriteBridge* bridge = getBridge();
    if (!bridge->initialize()) {
        LOGE("Failed to initialize Aseprite bridge");
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

// Shutdown
JNIEXPORT void JNICALL
Java_com_aseprite_android_AsepriteCore_shutdown(JNIEnv* env, jobject thiz) {
    LOGD("JNI: shutdown");
    if (g_bridge) {
        g_bridge->shutdown();
        g_bridge.reset();
    }
}

// Create sprite
JNIEXPORT jlong JNICALL
Java_com_aseprite_android_AsepriteCore_createSprite(JNIEnv* env, jobject thiz, jint width, jint height, jint colorMode) {
    LOGD("JNI: createSprite %dx%d mode=%d", width, height, colorMode);
    AsepriteBridge* bridge = getBridge();
    return static_cast<jlong>(bridge->createSprite(static_cast<int>(width), static_cast<int>(height), static_cast<int>(colorMode)));
}

// Open sprite
JNIEXPORT jlong JNICALL
Java_com_aseprite_android_AsepriteCore_openSprite(JNIEnv* env, jobject thiz, jstring filePath) {
    const char* path = env->GetStringUTFChars(filePath, nullptr);
    LOGD("JNI: openSprite %s", path);
    AsepriteBridge* bridge = getBridge();
    jlong result = static_cast<jlong>(bridge->openSprite(path));
    env->ReleaseStringUTFChars(filePath, path);
    return result;
}

// Save sprite
JNIEXPORT jboolean JNICALL
Java_com_aseprite_android_AsepriteCore_saveSprite(JNIEnv* env, jobject thiz, jlong spritePtr, jstring filePath) {
    const char* path = env->GetStringUTFChars(filePath, nullptr);
    LOGD("JNI: saveSprite %s", path);
    AsepriteBridge* bridge = getBridge();
    jboolean result = bridge->saveSprite(reinterpret_cast<uintptr_t>(spritePtr), path) ? JNI_TRUE : JNI_FALSE;
    env->ReleaseStringUTFChars(filePath, path);
    return result;
}

// Get width
JNIEXPORT jint JNICALL
Java_com_aseprite_android_AsepriteCore_getWidth(JNIEnv* env, jobject thiz, jlong spritePtr) {
    AsepriteBridge* bridge = getBridge();
    return static_cast<jint>(bridge->getWidth(reinterpret_cast<uintptr_t>(spritePtr)));
}

// Get height
JNIEXPORT jint JNICALL
Java_com_aseprite_android_AsepriteCore_getHeight(JNIEnv* env, jobject thiz, jlong spritePtr) {
    AsepriteBridge* bridge = getBridge();
    return static_cast<jint>(bridge->getHeight(reinterpret_cast<uintptr_t>(spritePtr)));
}

// Render frame
JNIEXPORT jobject JNICALL
Java_com_aseprite_android_AsepriteCore_renderFrame(JNIEnv* env, jobject thiz, jlong spritePtr, jint frameIndex) {
    LOGD("JNI: renderFrame %d", frameIndex);
    AsepriteBridge* bridge = getBridge();
    auto bitmap = bridge->renderFrame(reinterpret_cast<uintptr_t>(spritePtr), static_cast<int>(frameIndex));
    return bitmap ? bitmap : nullptr;
}

// Get frame count
JNIEXPORT jint JNICALL
Java_com_aseprite_android_AsepriteCore_getFrameCount(JNIEnv* env, jobject thiz, jlong spritePtr) {
    AsepriteBridge* bridge = getBridge();
    return static_cast<jint>(bridge->getFrameCount(reinterpret_cast<uintptr_t>(spritePtr)));
}

// Get layer count
JNIEXPORT jint JNICALL
Java_com_aseprite_android_AsepriteCore_getLayerCount(JNIEnv* env, jobject thiz, jlong spritePtr) {
    AsepriteBridge* bridge = getBridge();
    return static_cast<jint>(bridge->getLayerCount(reinterpret_cast<uintptr_t>(spritePtr)));
}

// Create layer
JNIEXPORT jlong JNICALL
Java_com_aseprite_android_AsepriteCore_createLayer(JNIEnv* env, jobject thiz, jlong spritePtr, jstring name) {
    const char* layerName = env->GetStringUTFChars(name, nullptr);
    LOGD("JNI: createLayer %s", layerName);
    AsepriteBridge* bridge = getBridge();
    jlong result = static_cast<jlong>(bridge->createLayer(reinterpret_cast<uintptr_t>(spritePtr), layerName));
    env->ReleaseStringUTFChars(name, layerName);
    return result;
}

// Delete layer
JNIEXPORT void JNICALL
Java_com_aseprite_android_AsepriteCore_deleteLayer(JNIEnv* env, jobject thiz, jlong spritePtr, jint layerIndex) {
    LOGD("JNI: deleteLayer %d", layerIndex);
    AsepriteBridge* bridge = getBridge();
    bridge->deleteLayer(reinterpret_cast<uintptr_t>(spritePtr), static_cast<int>(layerIndex));
}

// Get pixel
JNIEXPORT jint JNICALL
Java_com_aseprite_android_AsepriteCore_getPixel(JNIEnv* env, jobject thiz, jlong spritePtr, jint frame, jint layer, jint x, jint y) {
    AsepriteBridge* bridge = getBridge();
    return static_cast<jint>(bridge->getPixel(reinterpret_cast<uintptr_t>(spritePtr), static_cast<int>(frame), static_cast<int>(layer), static_cast<int>(x), static_cast<int>(y)));
}

// Set pixel
JNIEXPORT void JNICALL
Java_com_aseprite_android_AsepriteCore_setPixel(JNIEnv* env, jobject thiz, jlong spritePtr, jint frame, jint layer, jint x, jint y, jint color) {
    AsepriteBridge* bridge = getBridge();
    bridge->setPixel(reinterpret_cast<uintptr_t>(spritePtr), static_cast<int>(frame), static_cast<int>(layer), static_cast<int>(x), static_cast<int>(y), static_cast<int>(color));
}

// Undo
JNIEXPORT void JNICALL
Java_com_aseprite_android_AsepriteCore_undo(JNIEnv* env, jobject thiz, jlong spritePtr) {
    LOGD("JNI: undo");
    AsepriteBridge* bridge = getBridge();
    bridge->undo(reinterpret_cast<uintptr_t>(spritePtr));
}

// Redo
JNIEXPORT void JNICALL
Java_com_aseprite_android_AsepriteCore_redo(JNIEnv* env, jobject thiz, jlong spritePtr) {
    LOGD("JNI: redo");
    AsepriteBridge* bridge = getBridge();
    bridge->redo(reinterpret_cast<uintptr_t>(spritePtr));
}

// Can undo
JNIEXPORT jboolean JNICALL
Java_com_aseprite_android_AsepriteCore_canUndo(JNIEnv* env, jobject thiz, jlong spritePtr) {
    AsepriteBridge* bridge = getBridge();
    return bridge->canUndo(reinterpret_cast<uintptr_t>(spritePtr)) ? JNI_TRUE : JNI_FALSE;
}

// Can redo
JNIEXPORT jboolean JNICALL
Java_com_aseprite_android_AsepriteCore_canRedo(JNIEnv* env, jobject thiz, jlong spritePtr) {
    AsepriteBridge* bridge = getBridge();
    return bridge->canRedo(reinterpret_cast<uintptr_t>(spritePtr)) ? JNI_TRUE : JNI_FALSE;
}

// Export PNG
JNIEXPORT jboolean JNICALL
Java_com_aseprite_android_AsepriteCore_exportPNG(JNIEnv* env, jobject thiz, jlong spritePtr, jstring filePath) {
    const char* path = env->GetStringUTFChars(filePath, nullptr);
    LOGD("JNI: exportPNG %s", path);
    AsepriteBridge* bridge = getBridge();
    jboolean result = bridge->exportPNG(reinterpret_cast<uintptr_t>(spritePtr), path) ? JNI_TRUE : JNI_FALSE;
    env->ReleaseStringUTFChars(filePath, path);
    return result;
}

// Export GIF
JNIEXPORT jboolean JNICALL
Java_com_aseprite_android_AsepriteCore_exportGIF(JNIEnv* env, jobject thiz, jlong spritePtr, jstring filePath) {
    const char* path = env->GetStringUTFChars(filePath, nullptr);
    LOGD("JNI: exportGIF %s", path);
    AsepriteBridge* bridge = getBridge();
    jboolean result = bridge->exportGIF(reinterpret_cast<uintptr_t>(spritePtr), path) ? JNI_TRUE : JNI_FALSE;
    env->ReleaseStringUTFChars(filePath, path);
    return result;
}

// Export Sprite Sheet
JNIEXPORT jboolean JNICALL
Java_com_aseprite_android_AsepriteCore_exportSpriteSheet(JNIEnv* env, jobject thiz, jlong spritePtr, jstring filePath, jint columns) {
    const char* path = env->GetStringUTFChars(filePath, nullptr);
    LOGD("JNI: exportSpriteSheet %s cols=%d", path, columns);
    AsepriteBridge* bridge = getBridge();
    jboolean result = bridge->exportSpriteSheet(reinterpret_cast<uintptr_t>(spritePtr), path, static_cast<int>(columns)) ? JNI_TRUE : JNI_FALSE;
    env->ReleaseStringUTFChars(filePath, path);
    return result;
}

} // extern "C"