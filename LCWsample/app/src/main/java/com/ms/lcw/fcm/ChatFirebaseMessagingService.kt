package com.example.lcw.sdkdemo.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lcw.lsdk.chat.LiveChatMessaging
import com.lcw.lsdk.chat.persistence.ChatData
import com.ms.lcw.ChatActivity
import com.ms.lcw.Utility

class ChatFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Refreshed token: $token")
        Utility().storeFCMToken(this, "fcmtoken", token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Check if message contains a data payload
        remoteMessage.data.takeIf { it.isNotEmpty() }?.let {
            Log.d("FCM", "Message data payload: $it")
            showArrivalNotification(
                remoteMessage.notification?.title ?: "OC FCM Notification",
                remoteMessage.notification?.body ?: "This is a test message body."
            )
        }

        // Check if message contains a notification payload
        remoteMessage.notification?.let {
            Log.d("FCM", "Message Notification Body: ${it.body}")
            showArrivalNotification(
                it.title ?: "OC FCM Notification",
                it.body ?: "This is a test message body."
            )
        }
    }

    private fun showArrivalNotification(title: String, body: String) {
        if (LiveChatMessaging.getInstance().isChatInProgress && !ChatData.isMinimise()) {
            Log.d("FCM", "Notification bounce due to active chat screen")
            return
        }
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("notificationFlow", true)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // Create a NotificationChannel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        // Build and show the notification
        val builder = NotificationCompat.Builder(this, "arrival_notification_channel")
            .setSmallIcon(android.R.drawable.ic_notification_overlay)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Dismiss notification when tapped

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager?.notify(1, builder.build())
    }

    private fun createNotificationChannel() {
        val channelId = "arrival_notification_channel"
        val channelName = "Arrival Notifications"
        val channelDescription = "Notifications when you arrive at your destination"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDescription
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)
    }
}