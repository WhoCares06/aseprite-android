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

# Include directories
include_directories(
    ${CMAKE_SOURCE_DIR}/../../../aseprite-source/src
    ${CMAKE_SOURCE_DIR}/../../../aseprite-source/third_party
)

# Find Skia - we'll need to build or download it
# For now, we'll create a placeholder that shows the structure
# In practice, you'd need to build Skia for Android first

# Aseprite core library
add_library(aseprite_core STATIC
    # Core source files would go here
    # We'll create a minimal bridge first
    aseprite_bridge.cpp
)

# Main JNI library
add_library(aseprite SHARED
    jni_main.cpp
    aseprite_bridge.cpp
)

target_link_libraries(aseprite
    aseprite_core
    ${androidCmake_LIBRARIES}
    log
    android
    EGL
    GLESv3
    dl
)

# Find Skia
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