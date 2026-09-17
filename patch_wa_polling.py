with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/ui/erp/WhatsAppViewModel.kt', 'r') as f:
    vm = f.read()

poll_loop = """
    private var lastTopThreadId: String? = null
    private var lastMessagePreview: String? = null

    init {
        loadThreads()
        loadTemplates()
        startPolling()
    }

    private fun startPolling() {
        viewModelScope.launch {
            while(kotlinx.coroutines.isActive) {
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
                                    // It's a new message in a DIFFERENT thread!
                                    // We can trigger an event or just show a local notification via an injected context, 
                                    // but we can also just expose an event flow.
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
"""

if "startPolling" not in vm:
    vm = vm.replace('init {\n        loadThreads()\n        loadTemplates()\n    }', poll_loop)
    with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/ui/erp/WhatsAppViewModel.kt', 'w') as f:
        f.write(vm)

print("Polling added to ViewModel")
