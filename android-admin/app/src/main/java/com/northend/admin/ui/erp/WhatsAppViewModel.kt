package com.northend.admin.ui.erp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.northend.admin.data.remote.models.WhatsAppMessage
import com.northend.admin.data.remote.models.WhatsAppThread
import com.northend.admin.data.repository.AdminRepository
import com.northend.admin.utils.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
    private val repository: AdminRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WhatsAppUiState())
    val uiState: StateFlow<WhatsAppUiState> = _uiState.asStateFlow()

    init {
        loadThreads()
        loadTemplates()
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
}
