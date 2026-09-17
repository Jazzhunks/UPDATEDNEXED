package com.northend.admin.ui.erp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.northend.admin.data.remote.models.WhatsAppMessage
import com.northend.admin.data.remote.models.WhatsAppThread
import com.northend.admin.data.repository.AdminRepository
import com.northend.admin.utils.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.northend.admin.R
import com.northend.admin.ui.MainActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.isActive
import kotlinx.coroutines.delay
import javax.inject.Inject

data class WhatsAppUiState(
    val isLoadingThreads: Boolean = false,
    val isLoadingMessages: Boolean = false,
    val error: String? = null,
    val threads: List<WhatsAppThread> = emptyList(),
    val currentThread: WhatsAppThread? = null,
    val templates: List<com.northend.admin.data.remote.models.WhatsAppTemplate> = emptyList(),
    val messages: List<WhatsAppMessage> = emptyList()
)

@HiltViewModel
class WhatsAppViewModel @Inject constructor(
    private val repository: AdminRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(WhatsAppUiState())
    val uiState: StateFlow<WhatsAppUiState> = _uiState.asStateFlow()

    
    private var lastTopThreadId: String? = null
    private var lastMessagePreview: String? = null

    init {
        loadThreads()
        loadTemplates()
        startPolling()
    }

    private fun startPolling() {
        viewModelScope.launch {
            while(isActive) {
                kotlinx.coroutines.delay(3000)
                // 1. Poll Threads
                when (val res = repository.listWhatsAppThreads()) {
                    is ResultWrapper.Success -> {
                        val newThreads = res.data
                        _uiState.value = _uiState.value.copy(threads = newThreads)
                        
                        // Check for new messages in OTHER threads for local notification
                        val topThread = newThreads.firstOrNull()
                        if (topThread != null) {
                            if (lastTopThreadId != null && (topThread.id != lastTopThreadId || topThread.lastMessagePreview != lastMessagePreview)) {
                                if (topThread.id != _uiState.value.currentThread?.id) {
                                    // Post a REAL system notification!
                                    val sender = topThread.contactName ?: topThread.studentName ?: topThread.phone ?: "WhatsApp"
                                    val preview = topThread.lastMessagePreview ?: "New message"
                                    showSystemNotification(sender, preview)
                                }
                            }
                            lastTopThreadId = topThread.id
                            lastMessagePreview = topThread.lastMessagePreview
                        }
                    }
                    else -> {}
                }
                
                // 2. Poll Messages
                _uiState.value.currentThread?.let { thread ->
                    when (val res = repository.getWhatsAppMessages(thread.id)) {
                        is ResultWrapper.Success -> {
                            _uiState.value = _uiState.value.copy(messages = res.data)
                        }
                        else -> {}
                    }
                }
            }
        }
    }


    
    fun loadTemplates() {
        viewModelScope.launch {
            when (val res = repository.getWhatsAppTemplates()) {
                is ResultWrapper.Success -> _uiState.value = _uiState.value.copy(templates = res.data)
                else -> {}
            }
        }
    }

    fun loadThreads() {
        _uiState.value = _uiState.value.copy(isLoadingThreads = true, error = null)
        viewModelScope.launch {
            when (val res = repository.listWhatsAppThreads()) {
                is ResultWrapper.Success -> _uiState.value = _uiState.value.copy(isLoadingThreads = false, threads = res.data)
                is ResultWrapper.Error -> _uiState.value = _uiState.value.copy(isLoadingThreads = false, error = res.message)
                else -> {}
            }
        }
    }

    fun selectThread(thread: WhatsAppThread) {
        _uiState.value = _uiState.value.copy(currentThread = thread, isLoadingMessages = true)
        viewModelScope.launch {
            when (val res = repository.getWhatsAppMessages(thread.id)) {
                is ResultWrapper.Success -> _uiState.value = _uiState.value.copy(isLoadingMessages = false, messages = res.data)
                is ResultWrapper.Error -> _uiState.value = _uiState.value.copy(isLoadingMessages = false, error = res.message)
                else -> {}
            }
        }
    }

    fun deselectThread() {
        _uiState.value = _uiState.value.copy(currentThread = null, messages = emptyList())
        loadThreads()
        loadTemplates()
    }

    
    fun startNewChat(phone: String, templateName: String = "result_notification") {
        viewModelScope.launch {
            when (val res = repository.sendDirectWhatsApp(phone, templateName)) {
                is ResultWrapper.Success -> {
                    loadThreads() // Reload to get the new thread in the list
                }
                is ResultWrapper.Error -> _uiState.value = _uiState.value.copy(error = res.message)
                else -> {}
            }
        }
    }

    
    fun sendTemplateMessage(templateName: String) {
        val threadId = _uiState.value.currentThread?.id ?: return
        viewModelScope.launch {
            // Re-use sendDirectWhatsApp logic, but wait, sendDirect takes a phone number.
            // If we are in a thread, we can use a new endpoint or just send a normal message with kind="template" if backend supports it.
            // But wait, the backend `send_message` ONLY accepts kind="text".
            // Let's use `sendDirectWhatsApp` by passing the thread's phone number!
            val phone = _uiState.value.currentThread?.phone ?: return@launch
            when (val res = repository.sendDirectWhatsApp(phone, templateName)) {
                is ResultWrapper.Success -> selectThread(_uiState.value.currentThread!!) // reload messages
                else -> {}
            }
        }
    }

    fun sendMessage(text: String) {
        val threadId = _uiState.value.currentThread?.id ?: return
        viewModelScope.launch {
            when (val res = repository.sendWhatsAppMessage(threadId, text)) {
                is ResultWrapper.Success -> {
                    val updatedMsgs = _uiState.value.messages + res.data
                    _uiState.value = _uiState.value.copy(messages = updatedMsgs)
                }
                is ResultWrapper.Error -> _uiState.value = _uiState.value.copy(error = res.message)
                else -> {}
            }
        }
    }

    private fun showSystemNotification(title: String, message: String) {
        val channelId = "northend_notifications"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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

        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(context, channelId)
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
