plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.andris.snauda"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.andris.snauda"
        minSdk = 26          // Android 8+
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}
// Nav ārēju bibliotēku — tikai Android SDK.
