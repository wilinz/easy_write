# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keepclassmembers class kotlin.Metadata {
    public *;
}

-keepclassmembers class * {
    @com.squareup.moshi.FromJson *;
    @com.squareup.moshi.ToJson *;
}

-keep class com.eazywrite.app.data.model.** {*;}

#-keep class com.github.aachartmodel.** {*;}
#
#-dontwarn com.github.aachartmodel.**

# Kotlin
-dontwarn kotlin.**
-keep class kotlin.** { *; }
-keepclasseswithmembers class kotlin.Metadata {
    public <methods>;
}

# Coroutines
-dontwarn kotlinx.coroutines.**
-keepclassmembers class kotlinx.coroutines.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.coroutines.* {
    *;
}

# Room
-keepattributes Signature
-keepattributes *Annotation*
-keep class androidx.room.** {
    *;
}
-keepclasseswithmembers class * extends androidx.room.RoomDatabase {
    public static *** getInstance(...);
}
-keepclasseswithmembers class * extends androidx.room.RoomDatabase {
    private static final java.util.concurrent.Executor INSTANCE;
    public static *** execute(...);
    public static *** query(...);
    public static *** getGeneratedImplementation(...);
}
-keepclassmembers class * extends androidx.room.RoomDatabase {
    private boolean mDatabaseCreated;
    private void checkDatabaseExists(android.content.Context);
    public void clearAllTables();
}

# Retrofit
-dontwarn okio.**
-dontwarn javax.annotation.**

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
    <init>(...);
}
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

# Coil
-keep public class org.intellij.lang.annotations.** {
  *;
}
-keep class coil.sharp.** {
    *;
}
-keep class androidx.compose.runtime.*
-keep class androidx.compose.* {
    *;
}

# AgentWeb
-keep class com.just.agentweb.** { *; }

# ML Kit
-dontwarn com.google.mlkit.**

# Markwon
-keep class org.commonmark.internal.** { *; }

# Storage
-keep class com.anggrayudi.storage.** { *; }

# AAChartCore-Kotlin
-keep class com.github.aachartmodel.aainfographics.** { *; }
-keepclassmembers class com.github.aachartmodel.aainfographics.** {
    *;
}

# OpenCC
-keep class opencc.** { *; }

# ColorPickerView
-keep class com.skydoves.colorpickerview.** { *; }

# SpeedDial
-keep class com.leinardi.android.** { *; }

# Moshi
-keep class com.squareup.moshi.** { *; }

# CSV
-keep class com.github.doyaaaaaken.** { *; }

-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}