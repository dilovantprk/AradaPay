# ProGuard / R8 Rules for AradaPay

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Jetpack Compose & Material 3
-keep class androidx.compose.material3.** { *; }
-dontwarn androidx.compose.**

# Hilt / Dagger
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
-keepclassmembers class * {
    @javax.inject.Inject *;
}

# Firebase & Play Services
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-dontwarn com.google.firebase.**
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Data Models (Firestore mapping)
-keepclassmembers class com.ardabank.aradapay.data.model.** {
    public <fields>;
    public <methods>;
}
-keepclassmembers class com.ardabank.aradapay.domain.model.** {
    public <fields>;
    public <methods>;
}

# ZXing / Barcode
-keep class com.google.zxing.** { *; }
-dontwarn com.google.zxing.**
