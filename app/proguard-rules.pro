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
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# ===========================
# Reglas para Kotlin
# ===========================
-keep class kotlin.Metadata { *; }
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# ===========================
# Reglas para Coroutines
# ===========================
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**

# ===========================
# Reglas para ViewModel (MVVM)
# ===========================
-keep class * extends androidx.lifecycle.ViewModel {
    <init>();
}
-keep class * extends androidx.lifecycle.AndroidViewModel {
    <init>(android.app.Application);
}
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keepclassmembers class * extends androidx.lifecycle.AndroidViewModel {
    <init>(...);
}

# ===========================
# Reglas para Jetpack Compose
# ===========================
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.foundation.** { *; }
-keep class androidx.compose.material3.** { *; }
-keep class androidx.compose.animation.** { *; }

# Mantener funciones Composable
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

# Mantener anotaciones de Compose
-keepattributes *Annotation*
-keep @androidx.compose.runtime.Composable class * { *; }
-keep @androidx.compose.runtime.Stable class * { *; }
-keep @androidx.compose.runtime.Immutable class * { *; }

-dontwarn androidx.compose.**

# ===========================
# Reglas para Navigation Compose
# ===========================
-keep class androidx.navigation.** { *; }

# ===========================
# Reglas para DataStore y Serialización
# ===========================
# NOTA: El linter puede mostrar "Unresolved class name" pero esto es normal.
# ProGuard procesará estas reglas correctamente en runtime ya que la dependencia
# androidx.datastore:datastore-preferences:1.1.7 está incluida en el proyecto.

# Mantener todas las clases relacionadas con DataStore
-keep,allowobfuscation class androidx.datastore.** { *; }

# Mantener métodos y campos de Preferences específicamente
-keepclassmembers class * {
    androidx.datastore.core.DataStore *;
}

# Mantener atributos necesarios para serialización de Preferences
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations

# Suprimir warnings esperados (no afectan funcionalidad)
-dontwarn androidx.datastore.**
-dontwarn kotlinx.coroutines.flow.**

# ===========================
# Reglas para Clases de Datos (Models)
# ===========================
-keep class dall.full.level.app.data.** { *; }
-keepclassmembers class dall.full.level.app.data.** {
    <init>(...);
    <fields>;
}

# ===========================
# Reglas para ViewModels de la App
# ===========================
-keep class dall.full.level.app.viewmodel.** { *; }
-keepclassmembers class dall.full.level.app.viewmodel.** {
    <init>(...);
}

# ===========================
# Reglas para Repositories
# ===========================
-keep class dall.full.level.app.repository.** { *; }
-keepclassmembers class dall.full.level.app.repository.** {
    <init>(...);
}

# ===========================
# Reglas para Sensores de Android
# ===========================
-keep class android.hardware.Sensor { *; }
-keep class android.hardware.SensorManager { *; }
-keep class android.hardware.SensorEvent { *; }
-keep class android.hardware.SensorEventListener { *; }

# ===========================
# Reglas para Vibración
# ===========================
-keep class android.os.Vibrator { *; }
-keep class android.os.VibratorManager { *; }
-keep class android.os.VibrationEffect { *; }

# ===========================
# Reglas para Reflection
# ===========================
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# ===========================
# Reglas para Enums
# ===========================
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ===========================
# Reglas para Serialización
# ===========================
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ===========================
# Reglas para Parcelable
# ===========================
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# ===========================
# Optimizaciones Adicionales
# ===========================
# Eliminar logs en release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# Optimizar código Kotlin
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
    static void checkExpressionValueIsNotNull(java.lang.Object, java.lang.String);
    static void checkNotNullExpressionValue(java.lang.Object, java.lang.String);
    static void checkReturnedValueIsNotNull(java.lang.Object, java.lang.String, java.lang.String);
    static void checkFieldIsNotNull(java.lang.Object, java.lang.String, java.lang.String);
    static void checkNotNullParameter(java.lang.Object, java.lang.String);
}

# ===========================
# Advertencias que se pueden ignorar
# ===========================
-dontwarn org.jetbrains.annotations.**
-dontwarn javax.annotation.**
-dontwarn java.lang.instrument.**