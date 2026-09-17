import re

with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/ui/erp/WhatsAppInboxScreen.kt', 'r') as f:
    ui = f.read()

# 1. Update ThreadItem
old_thread_item = """fun ThreadItem(thread: WhatsAppThread, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = thread.contactName ?: thread.studentName ?: thread.phone ?: "Unknown",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = thread.lastMessagePreview ?: "",
                fontSize = 14.sp,
                color = Color.Gray,
                maxLines = 1
            )
        }
    }
}"""

new_thread_item = """fun ThreadItem(thread: WhatsAppThread, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = thread.contactName ?: thread.studentName ?: thread.phone ?: "Unknown",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = thread.lastMessagePreview ?: "",
                fontSize = 14.sp,
                color = if (thread.unreadCount > 0) Color.Black else Color.Gray,
                fontWeight = if (thread.unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1
            )
        }
        if (thread.unreadCount > 0) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier.size(24.dp).clip(CircleShape).background(WhatsAppLightGreen),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = thread.unreadCount.toString(),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}"""

ui = ui.replace(old_thread_item, new_thread_item)


# 2. Update topNotification logic
old_notif_logic = """    var topNotification by remember { mutableStateOf<String?>(null) }
    
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

new_notif_logic = """    var topNotificationThread by remember { mutableStateOf<com.northend.admin.data.remote.models.WhatsAppThread?>(null) }
    
    LaunchedEffect(state.threads) {
        val topThread = state.threads.firstOrNull()
        if (topThread != null) {
            if (lastTopThreadId != null && (topThread.id != lastTopThreadId || topThread.lastMessagePreview != lastPreview)) {
                if (topThread.id != state.currentThread?.id) {
                    topNotificationThread = topThread
                }
            }
            lastTopThreadId = topThread.id
            lastPreview = topThread.lastMessagePreview
        }
    }

    LaunchedEffect(topNotificationThread) {
        if (topNotificationThread != null) {
            delay(3000)
            topNotificationThread = null
        }
    }"""

ui = ui.replace(old_notif_logic, new_notif_logic)


# 3. Update topNotification UI
old_notif_ui = """        AnimatedVisibility(
            visible = topNotification != null,
            enter = slideInVertically(initialOffsetY = { -it }),
            exit = slideOutVertically(targetOffsetY = { -it }),
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 40.dp).padding(horizontal = 16.dp)
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
        }"""

new_notif_ui = """        AnimatedVisibility(
            visible = topNotificationThread != null,
            enter = slideInVertically(initialOffsetY = { -it }),
            exit = slideOutVertically(targetOffsetY = { -it }),
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 40.dp).padding(horizontal = 16.dp)
        ) {
            Surface(
                color = WhatsAppTeal,
                shape = RoundedCornerShape(8.dp),
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth().clickable {
                    topNotificationThread?.let {
                        viewModel.selectThread(it)
                        topNotificationThread = null
                    }
                }
            ) {
                Text(
                    text = "New message from ${topNotificationThread?.contactName ?: topNotificationThread?.phone}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }"""

ui = ui.replace(old_notif_ui, new_notif_ui)


with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/ui/erp/WhatsAppInboxScreen.kt', 'w') as f:
    f.write(ui)
print("UI Patched with unread badges and clickable notification")
