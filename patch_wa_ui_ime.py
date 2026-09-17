with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/ui/erp/WhatsAppInboxScreen.kt', 'r') as f:
    ui = f.read()

# Fix 1: imePadding for Scaffold
if "modifier = Modifier.imePadding().systemBarsPadding()," not in ui:
    ui = ui.replace('Scaffold(\n        topBar = {', 'Scaffold(\n        modifier = Modifier.imePadding().systemBarsPadding(),\n        topBar = {')
    ui = ui.replace('Scaffold(\n            topBar = {', 'Scaffold(\n            modifier = Modifier.imePadding().systemBarsPadding(),\n            topBar = {')

# Fix 2: In-app notification for new messages
toast_logic = """
    val context = androidx.compose.ui.platform.LocalContext.current
    var lastTopThreadId by remember { mutableStateOf<String?>(null) }
    var lastPreview by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(state.threads) {
        val topThread = state.threads.firstOrNull()
        if (topThread != null) {
            if (lastTopThreadId != null && (topThread.id != lastTopThreadId || topThread.lastMessagePreview != lastPreview)) {
                if (topThread.id != state.currentThread?.id) {
                    android.widget.Toast.makeText(context, "New message from ${topThread.contactName ?: topThread.phone}", android.widget.Toast.LENGTH_LONG).show()
                }
            }
            lastTopThreadId = topThread.id
            lastPreview = topThread.lastMessagePreview
        }
    }
"""

if "val context =" not in ui:
    ui = ui.replace('val state by viewModel.uiState.collectAsState()\n    var showNewChatDialog by remember { mutableStateOf(false) }', 'val state by viewModel.uiState.collectAsState()\n    var showNewChatDialog by remember { mutableStateOf(false) }' + toast_logic)

with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/ui/erp/WhatsAppInboxScreen.kt', 'w') as f:
    f.write(ui)

print("UI Patched with imePadding and Toasts")
