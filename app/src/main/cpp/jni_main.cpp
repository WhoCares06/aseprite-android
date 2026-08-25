#include <jni.h>
#include <android/log.h>
#include <string>

#define LOG_TAG "AsepriteAndroid"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// Forward declarations for Aseprite core functions
// In a real port, these would link to the actual Aseprite source
extern "C" {

// Initialize Aseprite core
JNIEXPORT jboolean JNICALL
Java_com_aseprite_android_AsepriteCore_initialize(JNIEnv* env, jobject thiz) {
    LOGD("Initializing Aseprite core...");
    // TODO: Initialize Aseprite core systems
    // app::App::init();
    // os::EventQueue::init();
    return JNI_TRUE;
}

// Shutdown Aseprite core
JNIEXPORT void JNICALL
Java_com_aseprite_android_AsepriteCore_shutdown(JNIEnv* env, jobject thiz) {
    LOGD("Shutting down Aseprite core...");
    // TODO: Shutdown Aseprite core
    // app::App::shutdown();
}

// Create new sprite
JNIEXPORT jlong JNICALL
Java_com_aseprite_android_AsepriteCore_createSprite(JNIEnv* env, jobject thiz, 
                                                    jint width, jint height, 
                                                    jint colorMode) {
    LOGD("Creating sprite: %dx%d colorMode=%d", width, height, colorMode);
    // TODO: Create sprite using Aseprite core
    // doc::Sprite* sprite = new doc::Sprite(width, height, (doc::ColorMode)colorMode);
    // return reinterpret_cast<jlong>(sprite);
    return 0; // Placeholder
}

// Open sprite from file
JNIEXPORT jlong JNICALL
Java_com_aseprite_android_AsepriteCore_openSprite(JNIEnv* env, jobject thiz, 
                                                  jstring filePath) {
    const char* path = env->GetStringUTFChars(filePath, nullptr);
    LOGD("Opening sprite: %s", path);
    // TODO: Load sprite from file
    // doc::Sprite* sprite = app::App::instance()->openSprite(path);
    env->ReleaseStringUTFChars(filePath, path);
    return 0; // Placeholder
}

// Save sprite to file
JNIEXPORT jboolean JNICALL
Java_com_aseprite_android_AsepriteCore_saveSprite(JNIEnv* env, jobject thiz, 
                                                  jlong spritePtr, 
                                                  jstring filePath) {
    const char* path = env->GetStringUTFChars(filePath, nullptr);
    LOGD("Saving sprite to: %s", path);
    // TODO: Save sprite
    // doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    // app::App::instance()->saveSprite(sprite, path);
    env->ReleaseStringUTFChars(filePath, path);
    return JNI_FALSE; // Placeholder
}

// Get sprite width
JNIEXPORT jint JNICALL
Java_com_aseprite_android_AsepriteCore_getSpriteWidth(JNIEnv* env, jobject thiz, 
                                                      jlong spritePtr) {
    // doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    // return sprite->width();
    return 0;
}

// Get sprite height
JNIEXPORT jint JNICALL
Java_com_aseprite_android_AsepriteCore_getSpriteHeight(JNIEnv* env, jobject thiz, 
                                                       jlong spritePtr) {
    // doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    // return sprite->height();
    return 0;
}

// Render sprite frame to bitmap
JNIEXPORT jobject JNICALL
Java_com_aseprite_android_AsepriteCore_renderFrame(JNIEnv* env, jobject thiz, 
                                                   jlong spritePtr, 
                                                   jint frameIndex) {
    LOGD("Rendering frame %d", frameIndex);
    // TODO: Render frame using Skia
    // doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    // SkBitmap bitmap = renderFrame(sprite, frameIndex);
    // return createJavaBitmap(env, bitmap);
    return nullptr; // Placeholder
}

// Get frame count
JNIEXPORT jint JNICALL
Java_com_aseprite_android_AsepriteCore_getFrameCount(JNIEnv* env, jobject thiz, 
                                                     jlong spritePtr) {
    // doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    // return sprite->totalFrames();
    return 0;
}

// Get layer count
JNIEXPORT jint JNICALL
Java_com_aseprite_android_AsepriteCore_getLayerCount(JNIEnv* env, jobject thiz, 
                                                     jlong spritePtr) {
    // doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    // return sprite->root()->getLayerCount();
    return 0;
}

// Create new layer
JNIEXPORT jlong JNICALL
Java_com_aseprite_android_AsepriteCore_createLayer(JNIEnv* env, jobject thiz, 
                                                   jlong spritePtr, 
                                                   jstring layerName) {
    const char* name = env->GetStringUTFChars(layerName, nullptr);
    LOGD("Creating layer: %s", name);
    env->ReleaseStringUTFChars(layerName, name);
    return 0; // Placeholder
}

// Delete layer
JNIEXPORT void JNICALL
Java_com_aseprite_android_AsepriteCore_deleteLayer(JNIEnv* env, jobject thiz, 
                                                   jlong spritePtr, 
                                                   jint layerIndex) {
    LOGD("Deleting layer: %d", layerIndex);
    // TODO: Delete layer
}

// Get color at pixel
JNIEXPORT jint JNICALL
Java_com_aseprite_android_AsepriteCore_getPixel(JNIEnv* env, jobject thiz, 
                                                jlong spritePtr, 
                                                jint frame, jint layer, 
                                                jint x, jint y) {
    // doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    // doc::Color color = sprite->getPixel(frame, layer, x, y);
    // return color.toArgb();
    return 0;
}

// Set color at pixel
JNIEXPORT void JNICALL
Java_com_aseprite_android_AsepriteCore_setPixel(JNIEnv* env, jobject thiz, 
                                                jlong spritePtr, 
                                                jint frame, jint layer, 
                                                jint x, jint y, jint color) {
    LOGD("Setting pixel at (%d,%d) frame=%d layer=%d color=0x%X", x, y, frame, layer, color);
    // TODO: Set pixel
    // doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    // sprite->putPixel(frame, layer, x, y, doc::Color::fromArgb(color));
}

// Undo
JNIEXPORT void JNICALL
Java_com_aseprite_android_AsepriteCore_undo(JNIEnv* env, jobject thiz, 
                                            jlong spritePtr) {
    // doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    // sprite->getUndoHistory()->undo();
}

// Redo
JNIEXPORT void JNICALL
Java_com_aseprite_android_AsepriteCore_redo(JNIEnv* env, jobject thiz, 
                                            jlong spritePtr) {
    // doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    // sprite->getUndoHistory()->redo();
}

// Check if can undo
JNIEXPORT jboolean JNICALL
Java_com_aseprite_android_AsepriteCore_canUndo(JNIEnv* env, jobject thiz, 
                                               jlong spritePtr) {
    // return sprite->getUndoHistory()->canUndo() ? JNI_TRUE : JNI_FALSE;
    return JNI_FALSE;
}

// Check if can redo
JNIEXPORT jboolean JNICALL
Java_com_aseprite_android_AsepriteCore_canRedo(JNIEnv* env, jobject thiz, 
                                               jlong spritePtr) {
    // return sprite->getUndoHistory()->canRedo() ? JNI_TRUE : JNI_FALSE;
    return JNI_FALSE;
}

// Export as PNG
JNIEXPORT jboolean JNICALL
Java_com_aseprite_android_AsepriteCore_exportPNG(JNIEnv* env, jobject thiz, 
                                                 jlong spritePtr, 
                                                 jstring filePath) {
    const char* path = env->GetStringUTFChars(filePath, nullptr);
    LOGD("Exporting PNG to: %s", path);
    env->ReleaseStringUTFChars(filePath, path);
    return JNI_FALSE;
}

// Export as GIF
JNIEXPORT jboolean JNICALL
Java_com_aseprite_android_AsepriteCore_exportGIF(JNIEnv* env, jobject thiz, 
                                                 jlong spritePtr, 
                                                 jstring filePath) {
    const char* path = env->GetStringUTFChars(filePath, nullptr);
    LOGD("Exporting GIF to: %s", path);
    env->ReleaseStringUTFChars(filePath, path);
    return JNI_FALSE;
}

// Export as sprite sheet
JNIEXPORT jboolean JNICALL
Java_com_aseprite_android_AsepriteCore_exportSpriteSheet(JNIEnv* env, jobject thiz, 
                                                         jlong spritePtr, 
                                                         jstring filePath, 
                                                         jint columns) {
    const char* path = env->GetStringUTFChars(filePath, nullptr);
    LOGD("Exporting sprite sheet to: %s columns=%d", path, columns);
    env->ReleaseStringUTFChars(filePath, path);
    return JNI_FALSE;
}

} // extern "C"