cmake_minimum_required(VERSION 3.22.1)
project("aseprite-android" LANGUAGES CXX)

# C++ standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

# Android specific
if(ANDROID)
    set(CMAKE_ANDROID_STL c++_shared)
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -frtti -fexceptions -DANDROID")
endif()

# Aseprite core library (pure C++, no JNI)
add_library(aseprite_core STATIC
    aseprite_bridge.cpp
)

target_link_libraries(aseprite_core
    ${androidCmake_LIBRARIES}
    log
    android
)

# Main JNI library - only jni_main.cpp, NOT aseprite_bridge.cpp (already in aseprite_core)
add_library(aseprite SHARED
    jni_main.cpp
)

target_link_libraries(aseprite
    aseprite_core
    ${androidCmake_LIBRARIES}
    log
    android
    EGL
    GLESv3
    dl
    atomic
)

# Find Skia (from prebuilt SkiaSharp)
find_library(SKIA_LIB skia)
if(SKIA_LIB)
    target_link_libraries(aseprite ${SKIA_LIB})
    message(STATUS "Found Skia: ${SKIA_LIB}")
else()
    message(WARNING "Skia not found - you need to build Skia for Android first")
    message(WARNING "See: https://github.com/aseprite/skia")
endif()

# Set output directory
set_target_properties(aseprite PROPERTIES
    LIBRARY_OUTPUT_DIRECTORY ${CMAKE_SOURCE_DIR}/../../../jniLibs/${ANDROID_ABI}
)