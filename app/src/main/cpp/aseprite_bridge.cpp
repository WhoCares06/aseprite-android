// Aseprite Bridge - Connects JNI layer to Aseprite core
// This is a pure C++ class - NO JNI exports here
// JNI exports are in jni_main.cpp

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

// C-style interface for JNI (called from jni_main.cpp)
extern "C" {

// Bridge access
AsepriteBridge* getBridge() {
    return &AsepriteBridge::instance();
}

} // extern "C"