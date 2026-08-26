// Aseprite Bridge - C++ header for JNI layer
// This declares the bridge interface without JNI exports

#pragma once

#include <jni.h>
#include <string>

// Forward declare Aseprite types
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
    static AsepriteBridge& instance();
    
    bool initialize();
    void shutdown();
    bool isInitialized() const;
    
    // Sprite management
    doc::Sprite* createSprite(int width, int height, doc::ColorMode colorMode);
    doc::Sprite* openSprite(const char* path);
    bool saveSprite(doc::Sprite* sprite, const char* path);
    
    // Rendering
    skia::Bitmap* renderFrame(doc::Sprite* sprite, int frameIndex);
    
    // Undo/Redo
    void undo(doc::Sprite* sprite);
    void redo(doc::Sprite* sprite);
    bool canUndo(doc::Sprite* sprite);
    bool canRedo(doc::Sprite* sprite);
    
    // Export
    bool exportPNG(doc::Sprite* sprite, const char* path);
    bool exportGIF(doc::Sprite* sprite, const char* path);
    bool exportSpriteSheet(doc::Sprite* sprite, const char* path, int columns);

private:
    AsepriteBridge();
    ~AsepriteBridge();
    bool initialized_ = false;
};

// JNI Helper functions
namespace JNIUtil {
    jobject createJavaBitmap(JNIEnv* env, void* skiaBitmap);
    std::string jstringToStdString(JNIEnv* env, jstring jstr);
}

// C-style interface for JNI (called from jni_main.cpp)
extern "C" {
    AsepriteBridge* getBridge();
}