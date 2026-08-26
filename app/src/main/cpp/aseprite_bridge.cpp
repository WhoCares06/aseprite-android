// Aseprite Bridge - Connects JNI layer to Aseprite core
// This is a pure C++ class - NO JNI exports here
// JNI exports are in jni_main.cpp

#include "aseprite_bridge.h"

#include <android/log.h>

#define LOG_TAG "AsepriteBridge"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

// Bridge class implementation
AsepriteBridge::AsepriteBridge() : initialized_(false) {}

AsepriteBridge::~AsepriteBridge() {
    if (initialized_) shutdown();
}

AsepriteBridge& AsepriteBridge::instance() {
    static AsepriteBridge bridge;
    return bridge;
}

bool AsepriteBridge::initialize() {
    if (initialized_) return true;
    
    LOGD("Initializing Aseprite core...");
    
    // TODO: Actual initialization sequence:
    // 1. Initialize os::System
    // 2. Initialize os::EventQueue
    // 3. Initialize app::App
    // 4. Load Skia backend
    // 5. Initialize LAF (Layout and Fonts)
    
    // Example:
    // os::System::init();
    // os::EventQueue::init();
    // app::App::init();
    
    initialized_ = true;
    return true;
}

void AsepriteBridge::shutdown() {
    if (!initialized_) return;
    
    LOGD("Shutting down Aseprite core...");
    
    // TODO: Shutdown sequence
    // app::App::shutdown();
    // os::EventQueue::shutdown();
    // os::System::shutdown();
    
    initialized_ = false;
}

bool AsepriteBridge::isInitialized() const { return initialized_; }

doc::Sprite* AsepriteBridge::createSprite(int width, int height, doc::ColorMode colorMode) {
    if (!initialized_) return nullptr;
    
    // TODO: Create actual sprite
    // doc::Sprite* sprite = new doc::Sprite(width, height, colorMode);
    // return sprite;
    return nullptr;
}

doc::Sprite* AsepriteBridge::openSprite(const char* path) {
    if (!initialized_) return nullptr;
    
    // TODO: Load sprite from file
    // doc::Sprite* sprite = app::App::instance()->openSprite(path);
    return nullptr;
}

bool AsepriteBridge::saveSprite(doc::Sprite* sprite, const char* path) {
    if (!initialized_ || !sprite) return false;
    
    // TODO: Save sprite
    // app::App::instance()->saveSprite(sprite, path);
    return false;
}

skia::Bitmap* AsepriteBridge::renderFrame(doc::Sprite* sprite, int frameIndex) {
    if (!initialized_ || !sprite) return nullptr;
    
    // TODO: Render using Skia
    // doc::Frame* frame = sprite->getFrame(frameIndex);
    // Skia bitmap rendering...
    return nullptr;
}

void AsepriteBridge::undo(doc::Sprite* sprite) {
    if (!sprite) return;
    // sprite->getUndoHistory()->undo();
}

void AsepriteBridge::redo(doc::Sprite* sprite) {
    if (!sprite) return;
    // sprite->getUndoHistory()->redo();
}

bool AsepriteBridge::canUndo(doc::Sprite* sprite) {
    if (!sprite) return false;
    // return sprite->getUndoHistory()->canUndo();
    return false;
}

bool AsepriteBridge::canRedo(doc::Sprite* sprite) {
    if (!sprite) return false;
    // return sprite->getUndoHistory()->canRedo();
    return false;
}

bool AsepriteBridge::exportPNG(doc::Sprite* sprite, const char* path) {
    if (!initialized_ || !sprite) return false;
    // app::App::instance()->exportSprite(sprite, path, "png");
    return false;
}

bool AsepriteBridge::exportGIF(doc::Sprite* sprite, const char* path) {
    if (!initialized_ || !sprite) return false;
    // app::App::instance()->exportSprite(sprite, path, "gif");
    return false;
}

bool AsepriteBridge::exportSpriteSheet(doc::Sprite* sprite, const char* path, int columns) {
    if (!initialized_ || !sprite) return false;
    // app::App::instance()->exportSpriteSheet(sprite, path, columns);
    return false;
}

// JNI Helper functions implementation
namespace JNIUtil {

jobject createJavaBitmap(JNIEnv* env, void* skiaBitmap) {
    // TODO: Convert Skia bitmap to Android Bitmap
    // 1. Get Android Bitmap.Config.ARGB_8888
    // 2. Create Bitmap with createBitmap
    // 3. Copy pixels from Skia bitmap
    return nullptr;
}

std::string jstringToStdString(JNIEnv* env, jstring jstr) {
    if (!jstr) return "";
    const char* chars = env->GetStringUTFChars(jstr, nullptr);
    std::string result(chars);
    env->ReleaseStringUTFChars(jstr, chars);
    return result;
}

} // namespace JNIUtil

// C-style interface for JNI (called from jni_main.cpp)
extern "C" {

AsepriteBridge* getBridge() {
    return &AsepriteBridge::instance();
}

} // extern "C"