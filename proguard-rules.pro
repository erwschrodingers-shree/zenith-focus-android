# Jetpack Compose rules
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Keep Room Database
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# Keep Kotlin metadata
-keepclassmembers class ** {
    *** Companion;
}
-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep our app classes
-keep class com.zenith.focus.** { *; }
-dontwarn com.zenith.focus.**
