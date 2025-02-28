import org.jetbrains.kotlin.storage.CacheResetOnProcessCanceled.enabled
import java.net.URL

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

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
    }
    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    packagingOptions {
        pickFirst("**")
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }

}
repositories {
    flatDir {
        dirs("libs")
    }
}

// Download aar files from GitHub
val sdkVersion = "v1.0.1"
task("downloadAarFiles") {
    doLast {
        println("Download AARs task started...")
        val aar1Url = "https://github.com/microsoft/ContactCenterMessagingSDK-android/releases/download/$sdkVersion/ContactCenterMessagingWidget.aar"
        val aar2Url = "https://github.com/microsoft/ContactCenterMessagingSDK-android/releases/download/$sdkVersion/OmnichannelChatSDK.aar"

        val aar1File = file("${project.rootDir}/app/libs/ContactCenterMessagingWidget.aar")
        val aar2File = file("${project.rootDir}/app/libs/OmnichannelChatSDK.aar")

        URL(aar1Url).openStream().use { input -> aar1File.outputStream().use { output -> input.copyTo(output) } }
        URL(aar2Url).openStream().use { input -> aar2File.outputStream().use { output -> input.copyTo(output) } }
    }
}

tasks.named("preBuild") {
    dependsOn("downloadAarFiles")
}

dependencies {
    implementation(files("libs/ContactCenterMessagingWidget.aar"))
    implementation(files("libs/OmnichannelChatSDK.aar"))
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.facebook.react:react-native:+")
    implementation("org.webkit:android-jsc:+")
    implementation(project(":randombytes"))
    implementation(project(":randomvalues"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation ("io.adaptivecards:adaptivecards-android:2.9.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation ("com.google.android.flexbox:flexbox:3.0.0")

    //implementation (libs.firebase.analytics)
    implementation (libs.firebase.messaging.ktx)
    implementation(platform(libs.firebase.bom))

}