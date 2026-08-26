#include <jni.h>
#include <android/log.h>
#include <string>

#include "aseprite_bridge.cpp" // Include bridge for helper functions

#define LOG_TAG "AsepriteAndroid"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// JNI exports - delegate to AsepriteBridge

// Initialize Aseprite core
extern "C" JNIEXPORT jboolean JNICALL
Java_com_aseprite_android_AsepriteCore_initialize(JNIEnv* env, jobject thiz) {
    LOGD("Initializing Aseprite core...");
    AsepriteBridge* bridge = getBridge();
    return bridge->initialize() ? JNI_TRUE : JNI_FALSE;
}

// Shutdown Aseprite core
extern "C" JNIEXPORT void JNICALL
Java_com_aseprite_android_AsepriteCore_shutdown(JNIEnv* env, jobject thiz) {
    LOGD("Shutting down Aseprite core...");
    AsepriteBridge* bridge = getBridge();
    bridge->shutdown();
}

// Create new sprite
extern "C" JNIEXPORT jlong JNICALL
Java_com_aseprite_android_AsepriteCore_createSprite(JNIEnv* env, jobject thiz, 
                                                    jint width, jint height, 
                                                    jint colorMode) {
    LOGD("Creating sprite: %dx%d colorMode=%d", width, height, colorMode);
    AsepriteBridge* bridge = getBridge();
    doc::Sprite* sprite = bridge->createSprite(width, height, static_cast<doc::ColorMode>(colorMode));
    return reinterpret_cast<jlong>(sprite);
}

// Open sprite from file
extern "C" JNIEXPORT jlong JNICALL
Java_com_aseprite_android_AsepriteCore_openSprite(JNIEnv* env, jobject thiz, 
                                                  jstring filePath) {
    const char* path = env->GetStringUTFChars(filePath, nullptr);
    LOGD("Opening sprite: %s", path);
    AsepriteBridge* bridge = getBridge();
    doc::Sprite* sprite = bridge->openSprite(path);
    env->ReleaseStringUTFChars(filePath, path);
    return reinterpret_cast<jlong>(sprite);
}

// Save sprite to file
extern "C" JNIEXPORT jboolean JNICALL
Java_com_aseprite_android_AsepriteCore_saveSprite(JNIEnv* env, jobject thiz, 
                                                  jlong spritePtr, 
                                                  jstring filePath) {
    const char* path = env->GetStringUTFChars(filePath, nullptr);
    LOGD("Saving sprite to: %s", path);
    AsepriteBridge* bridge = getBridge();
    doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    jboolean result = bridge->saveSprite(sprite, path) ? JNI_TRUE : JNI_FALSE;
    env->ReleaseStringUTFChars(filePath, path);
    return result;
}

// Get sprite width
extern "C" JNIEXPORT jint JNICALL
Java_com_aseprite_android_AsepriteCore_getSpriteWidth(JNIEnv* env, jobject thiz, 
                                                      jlong spritePtr) {
    doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    return sprite ? 0 : 0; // sprite->width();
}

// Get sprite height
extern "C" JNIEXPORT jint JNICALL
Java_com_aseprite_android_AsepriteCore_getSpriteHeight(JNIEnv* env, jobject thiz, 
                                                       jlong spritePtr) {
    doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    return sprite ? 0 : 0; // sprite->height();
}

// Render sprite frame to bitmap
extern "C" JNIEXPORT jobject JNICALL
Java_com_aseprite_android_AsepriteCore_renderFrame(JNIEnv* env, jobject thiz, 
                                                   jlong spritePtr, 
                                                   jint frameIndex) {
    LOGD("Rendering frame %d", frameIndex);
    AsepriteBridge* bridge = getBridge();
    doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    skia::Bitmap* bitmap = bridge->renderFrame(sprite, frameIndex);
    return JNIUtil::createJavaBitmap(env, bitmap);
}

// Get frame count
extern "C" JNIEXPORT jint JNICALL
Java_com_aseprite_android_AsepriteCore_getFrameCount(JNIEnv* env, jobject thiz, 
                                                     jlong spritePtr) {
    doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    return sprite ? 0 : 0; // sprite->totalFrames();
}

// Get layer count
extern "C" JNIEXPORT jint JNICALL
Java_com_aseprite_android_AsepriteCore_getLayerCount(JNIEnv* env, jobject thiz, 
                                                     jlong spritePtr) {
    doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    return sprite ? 0 : 0; // sprite->root()->getLayerCount();
}

// Create new layer
extern "C" JNIEXPORT jlong JNICALL
Java_com_aseprite_android_AsepriteCore_createLayer(JNIEnv* env, jobject thiz, 
                                                   jlong spritePtr, 
                                                   jstring layerName) {
    const char* name = env->GetStringUTFChars(layerName, nullptr);
    LOGD("Creating layer: %s", name);
    env->ReleaseStringUTFChars(layerName, name);
    return 0; // Placeholder
}

// Delete layer
extern "C" JNIEXPORT void JNICALL
Java_com_aseprite_android_AsepriteCore_deleteLayer(JNIEnv* env, jobject thiz, 
                                                   jlong spritePtr, 
                                                   jint layerIndex) {
    LOGD("Deleting layer: %d", layerIndex);
    // TODO: Delete layer
}

// Get color at pixel
extern "C" JNIEXPORT jint JNICALL
Java_com_aseprite_android_AsepriteCore_getPixel(JNIEnv* env, jobject thiz, 
                                                jlong spritePtr, 
                                                jint frame, jint layer, 
                                                jint x, jint y) {
    return 0; // Placeholder
}

// Set color at pixel
extern "C" JNIEXPORT void JNICALL
Java_com_aseprite_android_AsepriteCore_setPixel(JNIEnv* env, jobject thiz, 
                                                jlong spritePtr, 
                                                jint frame, jint layer, 
                                                jint x, jint y, jint color) {
    LOGD("Setting pixel at (%d,%d) frame=%d layer=%d color=0x%X", x, y, frame, layer, color);
}

// Undo
extern "C" JNIEXPORT void JNICALL
Java_com_aseprite_android_AsepriteCore_undo(JNIEnv* env, jobject thiz, 
                                            jlong spritePtr) {
    AsepriteBridge* bridge = getBridge();
    doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    bridge->undo(sprite);
}

// Redo
extern "C" JNIEXPORT void JNICALL
Java_com_aseprite_android_AsepriteCore_redo(JNIEnv* env, jobject thiz, 
                                            jlong spritePtr) {
    AsepriteBridge* bridge = getBridge();
    doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    bridge->redo(sprite);
}

// Check if can undo
extern "C" JNIEXPORT jboolean JNICALL
Java_com_aseprite_android_AsepriteCore_canUndo(JNIEnv* env, jobject thiz, 
                                               jlong spritePtr) {
    AsepriteBridge* bridge = getBridge();
    doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    return bridge->canUndo(sprite) ? JNI_TRUE : JNI_FALSE;
}

// Check if can redo
extern "C" JNIEXPORT jboolean JNICALL
Java_com_aseprite_android_AsepriteCore_canRedo(JNIEnv* env, jobject thiz, 
                                               jlong spritePtr) {
    AsepriteBridge* bridge = getBridge();
    doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    return bridge->canRedo(sprite) ? JNI_TRUE : JNI_FALSE;
}

// Export as PNG
extern "C" JNIEXPORT jboolean JNICALL
Java_com_aseprite_android_AsepriteCore_exportPNG(JNIEnv* env, jobject thiz, 
                                                 jlong spritePtr, 
                                                 jstring filePath) {
    const char* path = env->GetStringUTFChars(filePath, nullptr);
    LOGD("Exporting PNG to: %s", path);
    AsepriteBridge* bridge = getBridge();
    doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    jboolean result = bridge->exportPNG(sprite, path) ? JNI_TRUE : JNI_FALSE;
    env->ReleaseStringUTFChars(filePath, path);
    return result;
}

// Export as GIF
extern "C" JNIEXPORT jboolean JNICALL
Java_com_aseprite_android_AsepriteCore_exportGIF(JNIEnv* env, jobject thiz, 
                                                 jlong spritePtr, 
                                                 jstring filePath) {
    const char* path = env->GetStringUTFChars(filePath, nullptr);
    LOGD("Exporting GIF to: %s", path);
    AsepriteBridge* bridge = getBridge();
    doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    jboolean result = bridge->exportGIF(sprite, path) ? JNI_TRUE : JNI_FALSE;
    env->ReleaseStringUTFChars(filePath, path);
    return result;
}

// Export as sprite sheet
extern "C" JNIEXPORT jboolean JNICALL
Java_com_aseprite_android_AsepriteCore_exportSpriteSheet(JNIEnv* env, jobject thiz, 
                                                         jlong spritePtr, 
                                                         jstring filePath, 
                                                         jint columns) {
    const char* path = env->GetStringUTFChars(filePath, nullptr);
    LOGD("Exporting sprite sheet to: %s columns=%d", path, columns);
    AsepriteBridge* bridge = getBridge();
    doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    jboolean result = bridge->exportSpriteSheet(sprite, path, columns) ? JNI_TRUE : JNI_FALSE;
    env->ReleaseStringUTFChars(filePath, path);
    return result;
}