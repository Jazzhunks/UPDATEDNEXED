with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/ui/erp/WhatsAppInboxScreen.kt', 'r') as f:
    ui = f.read()

# Add necessary imports
imports = """import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import kotlinx.coroutines.delay
"""
if "import androidx.compose.animation.AnimatedVisibility" not in ui:
    ui = ui.replace('import androidx.compose.foundation.background', imports + 'import androidx.compose.foundation.background')

# Find the LaunchedEffect and replace Toast with state update
old_effect = """LaunchedEffect(state.threads) {
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
    }"""

new_effect = """var topNotification by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(state.threads) {
        val topThread = state.threads.firstOrNull()
        if (topThread != null) {
            if (lastTopThreadId != null && (topThread.id != lastTopThreadId || topThread.lastMessagePreview != lastPreview)) {
                if (topThread.id != state.currentThread?.id) {
                    topNotification = "New message from ${topThread.contactName ?: topThread.phone}"
                }
            }
            lastTopThreadId = topThread.id
            lastPreview = topThread.lastMessagePreview
        }
    }

    LaunchedEffect(topNotification) {
        if (topNotification != null) {
            delay(3000)
            topNotification = null
        }
    }"""

ui = ui.replace(old_effect, new_effect)

# Wrap the main content in a Box and add the Animated top banner
main_content_start = "if (state.currentThread == null) {"
main_content_end = "if (showNewChatDialog) {"

# Wait, the structure is:
# @Composable
# fun WhatsAppInboxScreen(viewModel: WhatsAppViewModel = hiltViewModel()) {
#    ...
#    if (state.currentThread == null) { ... } else { ... }
#    if (showNewChatDialog) { ... }
# }

# So we can just wrap everything inside Box { ... }
box_wrapper = """Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (state.currentThread == null) {
"""

ui = ui.replace("if (state.currentThread == null) {", box_wrapper, 1)

# Now we need to close the Column and add the AnimatedVisibility overlay inside the Box
overlay_code = """
        } // End of Column

        // Custom Top Notification Banner
        AnimatedVisibility(
            visible = topNotification != null,
            enter = slideInVertically(initialOffsetY = { -it }),
            exit = slideOutVertically(targetOffsetY = { -it }),
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 40.dp).padding(horizontal = 16.dp).zIndex(100f)
        ) {
            Surface(
                color = WhatsAppTeal,
                shape = RoundedCornerShape(8.dp),
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = topNotification ?: "",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    } // End of Box
"""

ui = ui.replace("if (showNewChatDialog) {", overlay_code + "\n    if (showNewChatDialog) {")

if "import androidx.compose.ui.zIndex.zIndex" not in ui:
    ui = ui.replace('import androidx.compose.ui.Modifier', 'import androidx.compose.ui.Modifier\nimport androidx.compose.ui.zIndex.zIndex')

with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/ui/erp/WhatsAppInboxScreen.kt', 'w') as f:
    f.write(ui)

print("Top banner UI patched")
