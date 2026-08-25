// Aseprite Bridge - Connects JNI layer to Aseprite core
// This is a placeholder that shows the structure needed
// In production, this would include actual Aseprite headers and link to compiled libraries

#include <jni.h>
#include <android/log.h>
#include <string>
#include <memory>

// Forward declare Aseprite types to avoid including full headers
namespace doc {
    class Sprite;
    class Layer;
    class Frame;
    class Image;
    class Color;
    class Palette;
    class Tileset;
    class Tilemap;
    class UndoHistory;
    enum class ColorMode { RGB, GRAYSCALE, INDEXED };
}

namespace app {
    class App;
    class Context;
    class Tool;
    class ToolBox;
    class PaletteManager;
}

namespace os {
    class EventQueue;
    class Window;
    class Surface;
    class FileHandle;
    class System;
}

// Skia forward declarations
namespace skia {
    class Bitmap;
    class Canvas;
    class Paint;
    class Path;
    class Image;
}

// Bridge class to manage Aseprite core lifecycle
class AsepriteBridge {
public:
    static AsepriteBridge& instance() {
        static AsepriteBridge bridge;
        return bridge;
    }
    
    bool initialize() {
        if (initialized_) return true;
        
        __android_log_print(ANDROID_LOG_DEBUG, "AsepriteBridge", "Initializing Aseprite core...");
        
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
    
    void shutdown() {
        if (!initialized_) return;
        
        __android_log_print(ANDROID_LOG_DEBUG, "AsepriteBridge", "Shutting down Aseprite core...");
        
        // TODO: Shutdown sequence
        // app::App::shutdown();
        // os::EventQueue::shutdown();
        // os::System::shutdown();
        
        initialized_ = false;
    }
    
    bool isInitialized() const { return initialized_; }
    
    // Sprite management
    doc::Sprite* createSprite(int width, int height, doc::ColorMode colorMode) {
        if (!initialized_) return nullptr;
        
        // TODO: Create actual sprite
        // doc::Sprite* sprite = new doc::Sprite(width, height, colorMode);
        // return sprite;
        return nullptr;
    }
    
    doc::Sprite* openSprite(const char* path) {
        if (!initialized_) return nullptr;
        
        // TODO: Load sprite from file
        // doc::Sprite* sprite = app::App::instance()->openSprite(path);
        return nullptr;
    }
    
    bool saveSprite(doc::Sprite* sprite, const char* path) {
        if (!initialized_ || !sprite) return false;
        
        // TODO: Save sprite
        // app::App::instance()->saveSprite(sprite, path);
        return false;
    }
    
    // Rendering
    skia::Bitmap* renderFrame(doc::Sprite* sprite, int frameIndex) {
        if (!initialized_ || !sprite) return nullptr;
        
        // TODO: Render using Skia
        // doc::Frame* frame = sprite->getFrame(frameIndex);
        // Skia bitmap rendering...
        return nullptr;
    }
    
    // Undo/Redo
    void undo(doc::Sprite* sprite) {
        if (!sprite) return;
        // sprite->getUndoHistory()->undo();
    }
    
    void redo(doc::Sprite* sprite) {
        if (!sprite) return;
        // sprite->getUndoHistory()->redo();
    }
    
    bool canUndo(doc::Sprite* sprite) {
        if (!sprite) return false;
        // return sprite->getUndoHistory()->canUndo();
        return false;
    }
    
    bool canRedo(doc::Sprite* sprite) {
        if (!sprite) return false;
        // return sprite->getUndoHistory()->canRedo();
        return false;
    }
    
    // Export
    bool exportPNG(doc::Sprite* sprite, const char* path) {
        if (!initialized_ || !sprite) return false;
        // app::App::instance()->exportSprite(sprite, path, "png");
        return false;
    }
    
    bool exportGIF(doc::Sprite* sprite, const char* path) {
        if (!initialized_ || !sprite) return false;
        // app::App::instance()->exportSprite(sprite, path, "gif");
        return false;
    }
    
    bool exportSpriteSheet(doc::Sprite* sprite, const char* path, int columns) {
        if (!initialized_ || !sprite) return false;
        // app::App::instance()->exportSpriteSheet(sprite, path, columns);
        return false;
    }

private:
    AsepriteBridge() : initialized_(false) {}
    ~AsepriteBridge() { if (initialized_) shutdown(); }
    
    bool initialized_ = false;
};

// JNI Helper functions
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

// C-style interface for JNI
extern "C" {

// Bridge access
AsepriteBridge* getBridge() {
    return &AsepriteBridge::instance();
}

// Initialize
JNIEXPORT jboolean JNICALL
Java_com_aseprite_android_AsepriteCore_initialize(JNIEnv* env, jobject thiz) {
    return getBridge()->initialize() ? JNI_TRUE : JNI_FALSE;
}

// Shutdown
JNIEXPORT void JNICALL
Java_com_aseprite_android_AsepriteCore_shutdown(JNIEnv* env, jobject thiz) {
    getBridge()->shutdown();
}

// Create sprite
JNIEXPORT jlong JNICALL
Java_com_aseprite_android_AsepriteCore_createSprite(JNIEnv* env, jobject thiz, 
                                                    jint width, jint height, 
                                                    jint colorMode) {
    doc::Sprite* sprite = getBridge()->createSprite(
        width, height, static_cast<doc::ColorMode>(colorMode));
    return reinterpret_cast<jlong>(sprite);
}

// Open sprite
JNIEXPORT jlong JNICALL
Java_com_aseprite_android_AsepriteCore_openSprite(JNIEnv* env, jobject thiz, 
                                                  jstring filePath) {
    std::string path = JNIUtil::jstringToStdString(env, filePath);
    doc::Sprite* sprite = getBridge()->openSprite(path.c_str());
    return reinterpret_cast<jlong>(sprite);
}

// Save sprite
JNIEXPORT jboolean JNICALL
Java_com_aseprite_android_AsepriteCore_saveSprite(JNIEnv* env, jobject thiz, 
                                                  jlong spritePtr, 
                                                  jstring filePath) {
    doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    std::string path = JNIUtil::jstringToStdString(env, filePath);
    return getBridge()->saveSprite(sprite, path.c_str()) ? JNI_TRUE : JNI_FALSE;
}

// Get sprite width
JNIEXPORT jint JNICALL
Java_com_aseprite_android_AsepriteCore_getSpriteWidth(JNIEnv* env, jobject thiz, 
                                                      jlong spritePtr) {
    doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    return sprite ? 0 : 0; // sprite->width();
}

// Get sprite height
JNIEXPORT jint JNICALL
Java_com_aseprite_android_AsepriteCore_getSpriteHeight(JNIEnv* env, jobject thiz, 
                                                       jlong spritePtr) {
    doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    return sprite ? 0 : 0; // sprite->height();
}

// Render frame
JNIEXPORT jobject JNICALL
Java_com_aseprite_android_AsepriteCore_renderFrame(JNIEnv* env, jobject thiz, 
                                                   jlong spritePtr, 
                                                   jint frameIndex) {
    doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    skia::Bitmap* bitmap = getBridge()->renderFrame(sprite, frameIndex);
    return JNIUtil::createJavaBitmap(env, bitmap);
}

// Get frame count
JNIEXPORT jint JNICALL
Java_com_aseprite_android_AsepriteCore_getFrameCount(JNIEnv* env, jobject thiz, 
                                                     jlong spritePtr) {
    doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    return sprite ? 0 : 0; // sprite->totalFrames();
}

// Get layer count
JNIEXPORT jint JNICALL
Java_com_aseprite_android_AsepriteCore_getLayerCount(JNIEnv* env, jobject thiz, 
                                                     jlong spritePtr) {
    doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    return sprite ? 0 : 0; // sprite->root()->getLayerCount();
}

// Create layer
JNIEXPORT jlong JNICALL
Java_com_aseprite_android_AsepriteCore_createLayer(JNIEnv* env, jobject thiz, 
                                                   jlong spritePtr, 
                                                   jstring layerName) {
    // TODO: Implement
    return 0;
}

// Delete layer
JNIEXPORT void JNICALL
Java_com_aseprite_android_AsepriteCore_deleteLayer(JNIEnv* env, jobject thiz, 
                                                   jlong spritePtr, 
                                                   jint layerIndex) {
    // TODO: Implement
}

// Get pixel
JNIEXPORT jint JNICALL
Java_com_aseprite_android_AsepriteCore_getPixel(JNIEnv* env, jobject thiz, 
                                                jlong spritePtr, 
                                                jint frame, jint layer, 
                                                jint x, jint y) {
    // TODO: Implement
    return 0;
}

// Set pixel
JNIEXPORT void JNICALL
Java_com_aseprite_android_AsepriteCore_setPixel(JNIEnv* env, jobject thiz, 
                                                jlong spritePtr, 
                                                jint frame, jint layer, 
                                                jint x, jint y, jint color) {
    // TODO: Implement
}

// Undo
JNIEXPORT void JNICALL
Java_com_aseprite_android_AsepriteCore_undo(JNIEnv* env, jobject thiz, 
                                            jlong spritePtr) {
    doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    getBridge()->undo(sprite);
}

// Redo
JNIEXPORT void JNICALL
Java_com_aseprite_android_AsepriteCore_redo(JNIEnv* env, jobject thiz, 
                                            jlong spritePtr) {
    doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    getBridge()->redo(sprite);
}

// Can undo
JNIEXPORT jboolean JNICALL
Java_com_aseprite_android_AsepriteCore_canUndo(JNIEnv* env, jobject thiz, 
                                               jlong spritePtr) {
    doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    return getBridge()->canUndo(sprite) ? JNI_TRUE : JNI_FALSE;
}

// Can redo
JNIEXPORT jboolean JNICALL
Java_com_aseprite_android_AsepriteCore_canRedo(JNIEnv* env, jobject thiz, 
                                               jlong spritePtr) {
    doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    return getBridge()->canRedo(sprite) ? JNI_TRUE : JNI_FALSE;
}

// Export PNG
JNIEXPORT jboolean JNICALL
Java_com_aseprite_android_AsepriteCore_exportPNG(JNIEnv* env, jobject thiz, 
                                                 jlong spritePtr, 
                                                 jstring filePath) {
    doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    std::string path = JNIUtil::jstringToStdString(env, filePath);
    return getBridge()->exportPNG(sprite, path.c_str()) ? JNI_TRUE : JNI_FALSE;
}

// Export GIF
JNIEXPORT jboolean JNICALL
Java_com_aseprite_android_AsepriteCore_exportGIF(JNIEnv* env, jobject thiz, 
                                                 jlong spritePtr, 
                                                 jstring filePath) {
    doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    std::string path = JNIUtil::jstringToStdString(env, filePath);
    return getBridge()->exportGIF(sprite, path.c_str()) ? JNI_TRUE : JNI_FALSE;
}

// Export sprite sheet
JNIEXPORT jboolean JNICALL
Java_com_aseprite_android_AsepriteCore_exportSpriteSheet(JNIEnv* env, jobject thiz, 
                                                         jlong spritePtr, 
                                                         jstring filePath, 
                                                         jint columns) {
    doc::Sprite* sprite = reinterpret_cast<doc::Sprite*>(spritePtr);
    std::string path = JNIUtil::jstringToStdString(env, filePath);
    return getBridge()->exportSpriteSheet(sprite, path.c_str(), columns) ? JNI_TRUE : JNI_FALSE;
}

} // extern "C"