import re

with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/ui/erp/WhatsAppViewModel.kt', 'r') as f:
    vm = f.read()

vm_method = """
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
"""
if "startNewChat" not in vm:
    vm = vm.replace('fun sendMessage', vm_method + '\n    fun sendMessage')
    with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/ui/erp/WhatsAppViewModel.kt', 'w') as f:
        f.write(vm)

print("ViewModel patched")
