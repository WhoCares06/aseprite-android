# ProGuard rules for Aseprite Android

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep AsepriteCore class and its native methods
-keep class com.aseprite.android.AsepriteCore {
    *;
}

# Keep UI classes
-keep class com.aseprite.android.ui.** { *; }

# Keep CanvasView
-keep class com.aseprite.android.ui.CanvasView { *; }

# Keep EditorFragment
-keep class com.aseprite.android.ui.EditorFragment { *; }

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# Keep generated binding classes
-keep class com.aseprite.android.databinding.** { *; }

# Don't obfuscate R class
-keep class **.R$* {
    <fields>;
}

# Keep Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep Parcelable
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep JNI classes
-keep class * {
    native <methods>;
}

# Don't warn about missing Skia classes (will be resolved at runtime)
-dontwarn org.jetbrains.skia.**
-dontwarn org.skia.**