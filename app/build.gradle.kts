plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.aseprite.android"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.aseprite.android"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // NDK configuration
        externalNativeBuild {
            cmake {
                cppFlags += "-std=c++17 -frtti -fexceptions"
                arguments += "-DANDROID_STL=c++_shared"
                targets.add("aseprite")
            }
        }

        ndk {
            abiFilters.addAll(listOf("arm64-v8a", "armeabi-v7a", "x86", "x86_64"))
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    packagingOptions {
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.window:window:1.2.0")
    implementation("androidx.window:window-java:1.2.0")

    // UI components
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.drawerlayout:drawerlayout:1.2.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Skia (via SkiaSharp or manual)
    // implementation("org.jetbrains.skia:skia-android:115.0") // If available

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}