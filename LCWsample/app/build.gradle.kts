import org.jetbrains.kotlin.storage.CacheResetOnProcessCanceled.enabled

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

dependencies {
    /*implementation("crm.omnichannel.lcwsdk:OmnichannelChatSDK:0.0.0-pr.28")
    implementation("crm.omnichannel.lcwsdk:livechatwidget:0.0.1-pr.12")*/
    implementation(files("libs/ContactCenterMessagingWidget-0.0.2.aar"))
    implementation(files("libs/OmnichannelChatSDK-0.0.2.aar"))
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
}