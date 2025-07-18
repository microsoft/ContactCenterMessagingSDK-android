import java.net.URL

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}



// Android configuration for your application module.
android {
    namespace = "com.ms.lcw"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ms.lcwsamplapp"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Enable multi-dex to handle apps with a large number of methods.
        // This is often necessary for React Native apps.
        multiDexEnabled = true
        /*ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }*/

        // This `externalNativeBuild` block is only needed if your PURE ANDROID APP
        // directly builds its own native C++ code using CMake or NDK Build.
        // If your app only consumes native code from the React Native AAR,
        // you might not need this. However, to be safe, keep the STL argument.
    /*    externalNativeBuild {
            cmake {
                arguments("-DANDROID_STL=c++_shared")
            }
            // If you were using ndkBuild, uncomment and configure:
            // ndkBuild {
            //    arguments("-DANDROID_STL=c++_shared")
            // }
        }*/
    }
    packaging {
        pickFirst("**/*.so")
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            // Debug specific settings, e.g., enable minification for faster debug builds if needed
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

// Repositories for THIS app module. While also in top-level build.gradle,
// including it here ensures this module can resolve its direct dependencies.
repositories {
    google()
    mavenCentral()
    // Local 'libs' directory for AARs placed manually
    flatDir {
        dirs("libs")
    }
}

// Optional task to download AAR files. Currently commented out.
// If you manually place the AARs in `libs/`, keep this commented.
// If you want Gradle to download them, uncomment and ensure URLs are correct.
val sdkVersion = "v1.1.0" // Example version, adjust as needed
task("downloadAarFiles") {
     doLast {
         println("Download AARs task started...")
         val aar1Url = "https://github.com/microsoft/ContactCenterMessagingSDK-android/releases/download/$sdkVersion/ContactCenterMessagingWidget.aar"
         val aar2Url = "https://github.com/microsoft/ContactCenterMessagingSDK-android/releases/download/$sdkVersion/OmnichannelChatSDK.aar"

         val libsDir = file("${project.rootDir}/app/libs")
         libsDir.mkdirs() // Ensure the directory exists

         val aar1File = file("${libsDir}/ContactCenterMessagingWidget.aar")
         val aar2File = file("${libsDir}/OmnichannelChatSDK.aar")

         URL(aar1Url).openStream().use { input -> aar1File.outputStream().use { output -> input.copyTo(output) } }
         URL(aar2Url).openStream().use { input -> aar2File.outputStream().use { output -> input.copyTo(output) } }
         println("AARs downloaded successfully to ${libsDir.absolutePath}")
     }
}

// Ensure the download task runs before the build.
tasks.named("preBuild") {
    dependsOn("downloadAarFiles")
}

// Dependencies for your pure Android application.
dependencies {
    // Include your pre-built React Native AAR and other AARs manually placed in `libs/`.
    //implementation(files("libs/app-debug.aar"))

    // --- CRITICAL: Add SoLoader explicitly to the consuming app ---
    // This ensures the SoLoader class is available at runtime for the React Native code.
    //implementation("com.facebook.soloader:soloader:0.10.5")

    // --- NEW: Add react-android directly to the consuming app with exclusions ---
    // This ensures core React Native classes (like ReactPackage) are on the classpath.
    // Exclusions prevent native library duplication/conflicts with your AAR.
    implementation(files("libs/ContactCenterMessagingWidget.aar"))
    implementation(files("libs/OmnichannelChatSDK.aar"))
    implementation(libs.react.android)
    implementation(libs.jsc.android)
    // Google Flexbox Layout
    implementation(libs.flexbox)

    // Adaptive Cards (if used in your pure Android app)
    implementation (libs.adaptivecards.android)

    //adaptive card built locally with NSDK 28
    //implementation(files("libs/adaptivecards-ndk28.aar"))

    // Firebase dependencies (if used in your pure Android app)
    // implementation (libs.firebase.analytics) // Uncomment if needed
    implementation(libs.firebase.messaging.ktx) // Keep if used
    implementation(platform(libs.firebase.bom)) // Keep if used
    implementation(libs.gson)

    // Core AndroidX libraries (ensure these are aligned with your AAR's dependencies)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Kotlin Coroutines (often used with AndroidX libraries)
    implementation(libs.kotlinx.coroutines.android)

    // Test dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

}