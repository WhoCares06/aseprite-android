// Aseprite Bridge - Connects JNI layer to Aseprite core
// This is a pure C++ class - NO JNI exports here
// JNI exports are in jni_main.cpp

#include "aseprite_bridge.h"

#include <android/log.h>
#include <memory>
#include <unordered_map>
#include <mutex>

#define LOG_TAG "AsepriteAndroid"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// Forward declare Aseprite namespaces
namespace doc {
    class Sprite;
    class Layer;
    class Frame;
    class Image;
    enum class ColorMode : int;
    class Palette;
}

namespace app {
    class App;
}

// Dummy sprite for UI testing (replace with actual Aseprite types when integrated)
class DummySprite {
public:
    DummySprite(int w, int h) : width(w), height(h) {}
    int width;
    int height;
};

// Pimpl implementation
class AsepriteBridge::Impl {
public:
    Impl() : app(nullptr), nextId(1) {}
    ~Impl() {
        if (app) {
            delete app;
        }
    }

    app::App* app;
    std::unordered_map<uintptr_t, doc::Sprite*> sprites;
    uintptr_t nextId;
    std::mutex mutex;

    uintptr_t addSprite(doc::Sprite* sprite) {
        std::lock_guard<std::mutex> lock(mutex);
        uintptr_t id = nextId++;
        sprites[id] = sprite;
        return id;
    }

    doc::Sprite* getSprite(uintptr_t id) {
        std::lock_guard<std::mutex> lock(mutex);
        auto it = sprites.find(id);
        return (it != sprites.end()) ? it->second : nullptr;
    }

    void removeSprite(uintptr_t id) {
        std::lock_guard<std::mutex> lock(mutex);
        sprites.erase(id);
    }
};

AsepriteBridge::AsepriteBridge() : pImpl(std::make_unique<Impl>()) {}
AsepriteBridge::~AsepriteBridge() = default;

bool AsepriteBridge::initialize() {
    LOGD("Initializing Aseprite bridge");
    try {
        // TODO: Initialize actual Aseprite app instance
        // For now, return true to indicate success
        LOGD("Aseprite bridge initialized (stub)");
        return true;
    } catch (const std::exception& e) {
        LOGE("Failed to initialize: %s", e.what());
        return false;
    }
}

void AsepriteBridge::shutdown() {
    LOGD("Shutting down Aseprite bridge");
    std::lock_guard<std::mutex> lock(pImpl->mutex);
    for (auto& [id, sprite] : pImpl->sprites) {
        DummySprite* dummy = reinterpret_cast<DummySprite*>(sprite);
        delete dummy;
    }
    pImpl->sprites.clear();
    if (pImpl->app) {
        delete pImpl->app;
        pImpl->app = nullptr;
    }
}

doc::Sprite* AsepriteBridge::getSprite(uintptr_t spritePtr) {
    return pImpl->getSprite(spritePtr);
}

uintptr_t AsepriteBridge::createSprite(int width, int height, int colorMode) {
    LOGD("Creating sprite %dx%d colorMode=%d", width, height, colorMode);
    // Create a dummy sprite that can be used for UI testing
    // In a real implementation, this would create an actual Aseprite sprite
    DummySprite* sprite = new DummySprite(width, height);
    return pImpl->addSprite(reinterpret_cast<doc::Sprite*>(sprite));
}

uintptr_t AsepriteBridge::openSprite(const char* filePath) {
    LOGD("Opening sprite: %s", filePath);
    // TODO: Load actual Aseprite sprite from file
    // For now, create a dummy sprite with default size
    DummySprite* sprite = new DummySprite(256, 256);
    return pImpl->addSprite(reinterpret_cast<doc::Sprite*>(sprite));
}

bool AsepriteBridge::saveSprite(uintptr_t spritePtr, const char* filePath) {
    LOGD("Saving sprite to: %s", filePath);
    doc::Sprite* sprite = getSprite(spritePtr);
    if (!sprite) return false;
    // TODO: Save actual Aseprite sprite
    return true;
}

int AsepriteBridge::getWidth(uintptr_t spritePtr) {
    doc::Sprite* sprite = getSprite(spritePtr);
    if (!sprite) return 0;
    DummySprite* dummy = reinterpret_cast<DummySprite*>(sprite);
    return dummy->width;
}

int AsepriteBridge::getHeight(uintptr_t spritePtr) {
    doc::Sprite* sprite = getSprite(spritePtr);
    if (!sprite) return 0;
    DummySprite* dummy = reinterpret_cast<DummySprite*>(sprite);
    return dummy->height;
}

int AsepriteBridge::getFrameCount(uintptr_t spritePtr) {
    doc::Sprite* sprite = getSprite(spritePtr);
    if (!sprite) return 0;
    return 1;
}

int AsepriteBridge::getLayerCount(uintptr_t spritePtr) {
    doc::Sprite* sprite = getSprite(spritePtr);
    if (!sprite) return 0;
    return 1;
}

uintptr_t AsepriteBridge::createLayer(uintptr_t spritePtr, const char* name) {
    LOGD("Creating layer: %s", name);
    doc::Sprite* sprite = getSprite(spritePtr);
    if (!sprite) return 0;
    // TODO: Create actual layer
    return 1; // Return dummy layer ID
}

void AsepriteBridge::deleteLayer(uintptr_t spritePtr, int layerIndex) {
    LOGD("Deleting layer %d", layerIndex);
    // TODO: Delete actual layer
}

int AsepriteBridge::getPixel(uintptr_t spritePtr, int frame, int layer, int x, int y) {
    // TODO: Get actual pixel
    return 0xFF000000;
}

void AsepriteBridge::setPixel(uintptr_t spritePtr, int frame, int layer, int x, int y, int color) {
    LOGD("Setting pixel at (%d,%d) frame=%d layer=%d color=0x%08X", x, y, frame, layer, color);
    // TODO: Set actual pixel
}

void AsepriteBridge::undo(uintptr_t spritePtr) {
    LOGD("Undo");
    // TODO: Implement undo
}

void AsepriteBridge::redo(uintptr_t spritePtr) {
    LOGD("Redo");
    // TODO: Implement redo
}

bool AsepriteBridge::canUndo(uintptr_t spritePtr) {
    // TODO: Check if undo available
    return false;
}

bool AsepriteBridge::canRedo(uintptr_t spritePtr) {
    // TODO: Check if redo available
    return false;
}

bool AsepriteBridge::exportPNG(uintptr_t spritePtr, const char* filePath) {
    LOGD("Exporting PNG to: %s", filePath);
    // TODO: Export as PNG
    return true;
}

bool AsepriteBridge::exportGIF(uintptr_t spritePtr, const char* filePath) {
    LOGD("Exporting GIF to: %s", filePath);
    // TODO: Export as GIF
    return true;
}

bool AsepriteBridge::exportSpriteSheet(uintptr_t spritePtr, const char* filePath, int columns) {
    LOGD("Exporting Sprite Sheet to: %s columns=%d", filePath, columns);
    // TODO: Export as sprite sheet
    return true;
}