# Keep the Room Database instance
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public static volatile * INSTANCE;
}

# Keep all classes in your app package
-keep class com.example.walletai.** { *; }

# Keep ViewModel classes
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Keep AndroidViewModel classes
-keepclassmembers class * extends androidx.lifecycle.AndroidViewModel {
    <init>(android.app.Application);
}

# Keep Compose-related classes
-keepclassmembers class androidx.compose.** { *; }

# Keep Kotlin Coroutines
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Keep Hilt-related classes
-keepclasseswithmembers class * {
    @dagger.hilt.* <methods>;
}
-keep class dagger.hilt.** { *; }

# Keep Retrofit interfaces
-keep,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Keep enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}