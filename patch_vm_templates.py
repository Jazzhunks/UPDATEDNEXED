with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/ui/erp/WhatsAppViewModel.kt', 'r') as f:
    vm = f.read()

# Add templates to State
vm = vm.replace('val currentThread: WhatsAppThread? = null,', 'val currentThread: WhatsAppThread? = null,\n    val templates: List<com.northend.admin.data.remote.models.WhatsAppTemplate> = emptyList(),')

# Load templates in init
vm = vm.replace('loadThreads()\n    }', 'loadThreads()\n        loadTemplates()\n    }')

# Add loadTemplates function
load_tpl = """
    fun loadTemplates() {
        viewModelScope.launch {
            when (val res = repository.getWhatsAppTemplates()) {
                is ResultWrapper.Success -> _uiState.value = _uiState.value.copy(templates = res.data)
                else -> {}
            }
        }
    }
"""
if "fun loadTemplates" not in vm:
    vm = vm.replace('fun loadThreads()', load_tpl + '\n    fun loadThreads()')

# Add sendTemplateMessage function
send_tpl = """
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
"""
if "fun sendTemplateMessage" not in vm:
    vm = vm.replace('fun sendMessage', send_tpl + '\n    fun sendMessage')

with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/ui/erp/WhatsAppViewModel.kt', 'w') as f:
    f.write(vm)
