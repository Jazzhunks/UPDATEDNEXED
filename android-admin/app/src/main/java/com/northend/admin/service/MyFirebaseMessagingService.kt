package com.northend.admin.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.northend.admin.ui.MainActivity
import com.northend.admin.R
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val httpClient = OkHttpClient()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token: $token")
        sendTokenToBackend(token)
    }

    private fun sendTokenToBackend(token: String) {
        val json = JSONObject()
            .put("token", token)
            .put("platform", "android")
            .put("user_agent", "${Build.MODEL} / ${Build.VERSION.RELEASE}")
            .toString()

        val request = Request.Builder()
            .url("https://northendedu.com/api/admin/fcm-token")
            .post(json.toRequestBody("application/json; charset=utf-8".toMediaType()))
            .build()

        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.w("FCM", "Failed to register FCM token with backend", e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.w("FCM", "Failed to register FCM token: ${response.code}")
                }
                response.close()
            }
        })
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("FCM_DEBUG", "Message received from: ${remoteMessage.from}")
        Log.d("FCM_DEBUG", "Notification Payload: ${remoteMessage.notification?.title}, ${remoteMessage.notification?.body}")
        Log.d("FCM_DEBUG", "Data Payload: ${remoteMessage.data}")

        // Try to get title/body from notification object first
        var title = remoteMessage.notification?.title
        var body = remoteMessage.notification?.body

        // If not present in notification, check data payload
        if (title == null) {
            title = remoteMessage.data["title"] ?: remoteMessage.data["contact_name"] ?: remoteMessage.data["sender"] ?: "NorthEnd Update"
        }
        
        if (body == null) {
            body = remoteMessage.data["body"] ?: remoteMessage.data["message"] ?: remoteMessage.data["text"] ?: ""
            // Fallback: If it's a completely unformatted raw payload, just serialize it roughly
            if (body.isBlank() && remoteMessage.data.isNotEmpty()) {
                body = "New message received"
            }
        }

        val targetPath = remoteMessage.data["target_path"] // e.g. "/admin/whatsapp"

        // Let's only display notification if there's actually a message
        if (title.isNotBlank() || body.isNotBlank()) {
            sendNotification(title, body, targetPath)
        }
    }

        private fun sendNotification(title: String, messageBody: String, targetPath: String?) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            if (targetPath != null) {
                putExtra("target_path", targetPath)
            }
        }
        
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "northend_notifications"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        
        // We will just use the default application icon as a fallback if R.mipmap.ic_launcher is missing.
        // Actually, let's look up a valid built-in resource or standard mipmap to avoid compilation errors.
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "NorthEnd Admin Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}
