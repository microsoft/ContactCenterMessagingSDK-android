package com.ms.lcw

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class ChatApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //SoLoader.init(applicationContext, OpenSourceMergedSoMapping)
        //Manually initialize Firebase using FirebaseOptions
        //Refer your google-services.json
        val options = FirebaseOptions.Builder()
            .setApplicationId("your-app-id") // e.g. 1:1234567890:android:abcdef123456
            .setApiKey("your-api-key") // Your Firebase API key
            .setDatabaseUrl("your-database-url") // Your Firebase Database URL (optional)
            .setProjectId("your-project-id") // Your Firebase Project ID
            .setStorageBucket("your-storage-bucket") // Your Firebase Storage Bucket (optional)
            .build()
        FirebaseApp.initializeApp(this, options)
    }
}
