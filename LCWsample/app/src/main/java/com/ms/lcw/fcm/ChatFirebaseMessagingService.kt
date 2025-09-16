package com.example.lcw.sdkdemo.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lcw.lsdk.chat.LiveChatMessaging
import com.ms.lcw.ChatActivity
import com.ms.lcw.Utility
import org.json.JSONException
import org.json.JSONObject

class ChatFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Refreshed token: $token")
        Utility().storeFCMToken(this, "fcmtoken", token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val payload = remoteMessage.data
        remoteMessage.data.takeIf { it.isNotEmpty() }?.let {
            Log.d("FCM", "Message data payload: $it")
            try {
                val jsonString = try {
                    JSONObject(it).toString(4)
                } catch (e: JSONException) {
                    it ?: ""
                }
                // Show toast on main thread

            } catch (ex: Exception) {
                Log.e("FCM", "Error parsing notification: ${ex.message}")
            }
            showArrivalNotification(
                remoteMessage.notification?.title ?: "OC FCM Notification",
                remoteMessage.notification?.body ?: "This is a test message body."
            )
        }

        // Check if message contains a notification payload
        remoteMessage.notification?.let {
            Log.d("FCM", "Message Notification Body: ${it.body}")

            try {
                val intent = Intent("com.app.ACTION_FCM_MESSAGE")
                intent.putExtra("data_payload","Message Notification Body: ${it.body}")
                // Send broadcast
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                val jsonString = try {
                    JSONObject(it.body).toString(4)
                } catch (e: JSONException) {
                    it.body ?: ""
                }
                // Show toast on main thread
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(applicationContext, jsonString, Toast.LENGTH_LONG).show()
                }
            } catch (ex: Exception) {
                Log.e("FCM", "Error parsing notification: ${ex.message}")
            }
            showArrivalNotification(
                it.title ?: "OC FCM Notification",
                it.body ?: "This is a test message body."
            )
        }
    }

    private fun showArrivalNotification(title: String, body: String) {
        if (LiveChatMessaging.getInstance().isChatVisibleAndActive) {
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