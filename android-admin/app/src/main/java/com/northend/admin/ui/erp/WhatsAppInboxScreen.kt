package com.northend.admin.ui.erp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.northend.admin.data.remote.models.WhatsAppMessage
import com.northend.admin.data.remote.models.WhatsAppThread

val WhatsAppTeal = Color(0xFF075E54)
val WhatsAppLightGreen = Color(0xFF25D366)
val WhatsAppBackground = Color(0xFFECE5DD)
val WhatsAppOutgoing = Color(0xFFDCF8C6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhatsAppInboxScreen(viewModel: WhatsAppViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    var showNewChatDialog by remember { mutableStateOf(false) }
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


    if (state.currentThread == null) {
        // THREAD LIST
        Scaffold(
            modifier = Modifier.imePadding().systemBarsPadding(),
            topBar = {
                TopAppBar(
                    title = { Text("WhatsApp", color = Color.White, fontWeight = FontWeight.SemiBold) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = WhatsAppTeal)
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showNewChatDialog = true },
                    containerColor = WhatsAppLightGreen,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Filled.Message, "New Chat")
                }
            }
        ) { padding ->
            if (state.isLoadingThreads) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = WhatsAppTeal)
                }
            } else {
                LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
                    items(state.threads) { thread ->
                        ThreadItem(thread) { viewModel.selectThread(thread) }
                        Divider(color = Color.LightGray.copy(alpha = 0.3f), modifier = Modifier.padding(start = 72.dp))
                    }
                }
            }
        }
    } else {
        // CHAT SCREEN
        ChatScreen(
            viewModel = viewModel,
            thread = state.currentThread!!,
            messages = state.messages,
            onBack = { viewModel.deselectThread() },
            onSend = { viewModel.sendMessage(it) }
        )
    }

    if (showNewChatDialog) {
        var phone by remember { mutableStateOf("") }
        var selectedTemplate by remember { mutableStateOf("result_notification") }
        var expanded by remember { mutableStateOf(false) }
        AlertDialog(
            onDismissRequest = { showNewChatDialog = false },
            title = { Text("New Conversation") },
            text = {
                Column {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        placeholder = { Text("e.g. 919999999999") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedTemplate,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Template") },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = { IconButton(onClick = { expanded = true }) { Icon(androidx.compose.material.icons.Icons.Filled.List, null) } }
                        )
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            state.templates.forEach { tpl ->
                                DropdownMenuItem(text = { Text(tpl.name) }, onClick = {
                                    selectedTemplate = tpl.name
                                    expanded = false
                                })
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.startNewChat(phone, selectedTemplate)
                    showNewChatDialog = false
                }) {
                    Text("Start", color = WhatsAppTeal)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewChatDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun ThreadItem(thread: WhatsAppThread, onClick: () -> Unit) {
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: WhatsAppViewModel,
    thread: WhatsAppThread,
    messages: List<WhatsAppMessage>,
    onBack: () -> Unit,
    onSend: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    
    Scaffold(
        modifier = Modifier.imePadding().systemBarsPadding(),
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.LightGray), contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.Person, null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(thread.contactName ?: thread.phone ?: "Unknown", color = Color.White, fontSize = 18.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, null, tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WhatsAppTeal)
            )
        },
        containerColor = WhatsAppBackground
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                reverseLayout = true
            ) {
                items(messages.reversed()) { msg ->
                    ChatBubble(msg)
                }
            }
            
            // Input Bar
            var showTemplateMenu by remember { mutableStateOf(false) }
            Row(
                modifier = Modifier.padding(8.dp).fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                IconButton(onClick = { showTemplateMenu = true }, modifier = Modifier.background(Color.White, CircleShape)) {
                    Icon(androidx.compose.material.icons.Icons.Filled.List, "Templates", tint = Color.Gray)
                    DropdownMenu(expanded = showTemplateMenu, onDismissRequest = { showTemplateMenu = false }) {
                        val tpls = viewModel.uiState.collectAsState().value.templates
                        if (tpls.isEmpty()) DropdownMenuItem(text = { Text("No templates") }, onClick = {})
                        tpls.forEach { tpl ->
                            DropdownMenuItem(text = { Text(tpl.name) }, onClick = {
                                viewModel.sendTemplateMessage(tpl.name)
                                showTemplateMenu = false
                            })
                        }
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.weight(1f).background(Color.White, RoundedCornerShape(24.dp)),
                    placeholder = { Text("Message") },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                FloatingActionButton(
                    onClick = { 
                        if (text.isNotBlank()) {
                            onSend(text)
                            text = ""
                        }
                    },
                    modifier = Modifier.size(48.dp),
                    containerColor = WhatsAppTeal,
                    shape = CircleShape
                ) {
                    Icon(Icons.Filled.Send, "Send", tint = Color.White, modifier = Modifier.padding(start = 4.dp))
                }
            }
        }
    }
}

@Composable
fun ChatBubble(msg: WhatsAppMessage) {
    val isOutbound = msg.direction == "outbound"
    val bubbleColor = if (isOutbound) WhatsAppOutgoing else Color.White
    
    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), contentAlignment = if (isOutbound) Alignment.CenterEnd else Alignment.CenterStart) {
        Surface(
            color = bubbleColor,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isOutbound) 16.dp else 0.dp,
                bottomEnd = if (isOutbound) 0.dp else 16.dp
            ),
            shadowElevation = 1.dp
        ) {
            Text(
                text = msg.text ?: "[Media Message]",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                fontSize = 15.sp,
                color = Color.Black
            )
        }
    }
}
