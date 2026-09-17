package com.northend.admin.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.northend.admin.ui.theme.NorthEndTheme
import dagger.hilt.android.AndroidEntryPoint
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.northend.admin.worker.WhatsAppNotificationWorker
import com.northend.admin.data.repository.AdminRepository
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var adminRepository: AdminRepository

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Handle the permission result if needed
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // Permission is already granted
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // You can show a rationale here, but for now we'll just request directly
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun sendFcmTokenToBackend(token: String) {
        lifecycleScope.launch {
            try {
                adminRepository.registerFcmToken(token, "android")
            } catch (e: Exception) {
                Log.w("FCM", "Failed to register FCM token with backend", e)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        askNotificationPermission()

        // Schedule periodic background polling for new WhatsApp messages
        try {
            val workRequest = androidx.work.PeriodicWorkRequestBuilder<WhatsAppNotificationWorker>(
                15, TimeUnit.MINUTES
            ).build()
            androidx.work.WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "WhatsAppNotificationWork",
                androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        } catch (e: Exception) {
            Log.e("MainActivity", "Failed to schedule WhatsAppNotificationWorker", e)
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("FCM", "Current token: $token")
            sendFcmTokenToBackend(token)
        }
        
        // Subscribe to a general admin topic just in case backend uses it
        FirebaseMessaging.getInstance().subscribeToTopic("admin")

        val prefs = getSharedPreferences("crash_prefs", android.content.Context.MODE_PRIVATE)
        val lastCrash = prefs.getString("last_crash", null)
        if (lastCrash != null) {
            prefs.edit().remove("last_crash").apply()
            // We can pass this to a state and show a dialog, but for now let's just show an alert dialog.
            android.app.AlertDialog.Builder(this)
                .setTitle("Crash Log")
                .setMessage(lastCrash)
                .setPositiveButton("OK", null)
                .show()
        }

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            val sw = java.io.StringWriter()
            exception.printStackTrace(java.io.PrintWriter(sw))
            getSharedPreferences("crash_prefs", android.content.Context.MODE_PRIVATE)
                .edit()
                .putString("last_crash", sw.toString())
                .commit()
            defaultHandler?.uncaughtException(thread, exception)
        }

        
        setContent {
            NorthEndTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost()
                }
            }
        }
    }
}
