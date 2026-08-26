// Aseprite Bridge - C++ header for JNI layer
// This declares the bridge interface without JNI exports

#pragma once

#include <jni.h>
#include <string>
#include <android/bitmap.h>

// Forward declare Aseprite types
namespace doc {
    class Sprite;
    class Layer;
    class Frame;
    class Image;
    enum class ColorMode : int;
    class Palette;
}

class AsepriteBridge {
public:
    AsepriteBridge();
    ~AsepriteBridge();

    // Initialize/shutdown
    bool initialize();
    void shutdown();

    // Sprite management
    uintptr_t createSprite(int width, int height, int colorMode);
    uintptr_t openSprite(const char* filePath);
    bool saveSprite(uintptr_t spritePtr, const char* filePath);

    // Sprite properties
    int getWidth(uintptr_t spritePtr);
    int getHeight(uintptr_t spritePtr);
    int getFrameCount(uintptr_t spritePtr);
    int getLayerCount(uintptr_t spritePtr);

    // Internal: get sprite pointer (used by JNI layer)
    doc::Sprite* getSprite(uintptr_t spritePtr);

    // Frame rendering
    jobject renderFrame(JNIEnv* env, uintptr_t spritePtr, int frameIndex);

    // Layer operations
    uintptr_t createLayer(uintptr_t spritePtr, const char* name);
    void deleteLayer(uintptr_t spritePtr, int layerIndex);

    // Pixel operations
    int getPixel(uintptr_t spritePtr, int frame, int layer, int x, int y);
    void setPixel(uintptr_t spritePtr, int frame, int layer, int x, int y, int color);

    // Undo/Redo
    void undo(uintptr_t spritePtr);
    void redo(uintptr_t spritePtr);
    bool canUndo(uintptr_t spritePtr);
    bool canRedo(uintptr_t spritePtr);

    // Export
    bool exportPNG(uintptr_t spritePtr, const char* filePath);
    bool exportGIF(uintptr_t spritePtr, const char* filePath);
    bool exportSpriteSheet(uintptr_t spritePtr, const char* filePath, int columns);

private:
    class Impl;
    std::unique_ptr<Impl> pImpl;
};