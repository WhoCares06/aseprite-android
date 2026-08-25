# Aseprite Android

Aseprite pixel art editor ported to Android. This is a work-in-progress port of the [Aseprite](https://github.com/aseprite/aseprite) source code to Android using the NDK and JNI.

## Architecture

```
aseprite-android/
├── app/
│   ├── src/main/
│   │   ├── cpp/                 # JNI/C++ bridge to Aseprite core
│   │   │   ├── CMakeLists.txt   # CMake build configuration
│   │   │   ├── jni_main.cpp     # JNI entry points
│   │   │   └── aseprite_bridge.cpp  # Bridge to Aseprite core
│   │   ├── java/
│   │   │   └── com/aseprite/android/
│   │   │       ├── AsepriteCore.kt      # Kotlin wrapper for native methods
│   │   │       ├── AsepriteApplication.kt
│   │   │       ├── MainActivity.kt
│   │   │       ├── NewSpriteActivity.kt
│   │   │       └── ui/
│   │   │           ├── EditorFragment.kt
│   │   │           └── CanvasView.kt
│   │   ├── res/                 # Android resources
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── aseprite-source/             # Aseprite source (git submodule)
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── gradle/wrapper/
```

## Prerequisites

1. **Android Studio** (latest stable)
2. **Android NDK** (r26+)
3. **CMake** (3.22+)
4. **Skia for Android** - You need to build Skia for Android first:
   ```bash
   # See: https://github.com/aseprite/skia
   # Build Skia for Android ABIs: arm64-v8a, armeabi-v7a, x86, x86_64
   ```

## Building

### 1. Build Skia for Android (Required)

Before building this project, you must build Skia for Android:

```bash
# Clone Skia
git clone https://github.com/aseprite/skia.git
cd skia

# Checkout the aseprite-m124 branch
git checkout aseprite-m124

# Build for Android (using provided build scripts)
# This creates prebuilt libraries for all ABIs
python tools/git-sync-deps
bin/gn gen out/android_arm64 --args='target_os="android" target_cpu="arm64" is_debug=false'
ninja -C out/android_arm64

# Repeat for other ABIs: arm, x86, x64
```

### 2. Configure Skia Path

Edit `app/src/main/cpp/CMakeLists.txt` and set the `SKIA_DIR` to your Skia build directory:

```cmake
set(SKIA_DIR /path/to/skia/out/android_arm64)
```

Or pass it via CMake arguments in `build.gradle.kts`.

### 3. Build Android App

Open in Android Studio and build, or use command line:

```bash
./gradlew assembleDebug
```

## Current Status

This is a **skeleton project** with the Android app structure and JNI bridge in place. The actual Aseprite core compilation and integration requires:

1. ✅ Android project structure (Gradle, Manifest, Resources)
2. ✅ JNI bridge with native method declarations
3. ✅ Kotlin wrapper (`AsepriteCore.kt`)
4. ✅ Basic UI (MainActivity, EditorFragment, CanvasView)
5. ✅ Navigation (Drawer, Bottom toolbar, Tools/Layers panels)
6. ⏳ **Aseprite core compilation for Android** (requires Skia + CMake)
7. ⏳ **Skia integration** for rendering
8. ⏳ **Full feature implementation** (tools, layers, frames, onion skin, etc.)

## Next Steps

1. **Build Skia for Android** - This is the main blocker
2. **Integrate Aseprite source** - Add as CMake subdirectory or prebuilt library
3. **Implement JNI bridge** - Connect Java/Kotlin calls to Aseprite C++ core
4. **Implement CanvasView rendering** - Use Skia to render frames
5. **Add tools** - Brush, eraser, selection, fill, etc.
6. **Add file format support** - .aseprite, .png, .gif, .ase
8. **Test on devices** - Various screen sizes, Android versions

## License

Aseprite is licensed under its own [EULA](https://github.com/aseprite/aseprite/blob/main/EULA.txt). This Android port follows the same license terms.

## References

- [Aseprite Source](https://github.com/aseprite/aseprite)
- [Aseprite Skia Fork](https://github.com/aseprite/skia)
- [LAF Library](https://github.com/aseprite/laf)
- [Android NDK Guide](https://developer.android.com/ndk/guides)
- [JNI Tips](https://developer.android.com/training/articles/perf-jni)