package com.northend.admin.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.northend.admin.R
import com.northend.admin.data.local.TokenManager
import com.northend.admin.data.remote.AdminApiService
import com.northend.admin.di.NetworkModule
import com.northend.admin.ui.MainActivity
import com.northend.admin.utils.Constants
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class WhatsAppNotificationWorker(
    private val appContext: Context,
    workerParams: androidx.work.WorkerParameters
) : androidx.work.CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): androidx.work.ListenableWorker.Result {
        return try {
            val tokenManager = TokenManager(appContext)
            val token = tokenManager.getAccessToken() ?: return androidx.work.ListenableWorker.Result.success()

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                    chain.proceed(request)
                }
                .build()

            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

            val apiService = retrofit.create(AdminApiService::class.java)
            val response = apiService.listWhatsAppThreads(limit = 10)

            if (response.isSuccessful) {
                val threads = response.body() ?: emptyList()
                val topThread = threads.firstOrNull()

                if (topThread != null) {
                    val prefs = appContext.getSharedPreferences("whatsapp_worker_prefs", Context.MODE_PRIVATE)
                    val lastSavedId = prefs.getString("last_thread_id", null)
                    val lastSavedPreview = prefs.getString("last_message_preview", null)

                    val isNewMessage = lastSavedId != null && 
                            (topThread.id != lastSavedId || topThread.lastMessagePreview != lastSavedPreview)

                    if (isNewMessage) {
                        val sender = topThread.contactName ?: topThread.studentName ?: topThread.phone ?: "WhatsApp"
                        val preview = topThread.lastMessagePreview ?: "New message received"
                        
                        sendSystemNotification(sender, preview)
                    }

                    // Save latest thread state
                    prefs.edit()
                        .putString("last_thread_id", topThread.id)
                        .putString("last_message_preview", topThread.lastMessagePreview)
                        .apply()
                }
            }

            androidx.work.ListenableWorker.Result.success()
        } catch (e: Exception) {
            Log.e("WhatsAppWorker", "Error polling whatsapp threads in background", e)
            androidx.work.ListenableWorker.Result.retry()
        }
    }

    private fun sendSystemNotification(title: String, message: String) {
        val channelId = "northend_notifications"
        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "NorthEnd Admin Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(appContext, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            appContext,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
