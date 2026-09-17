with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/ui/erp/WhatsAppInboxScreen.kt', 'r') as f:
    ui = f.read()

# Add a Template icon to ChatScreen input bar
input_row_old = """Row(
                modifier = Modifier.padding(8.dp).fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {"""
input_row_new = """var showTemplateMenu by remember { mutableStateOf(false) }
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
                Spacer(modifier = Modifier.width(8.dp))"""
if "var showTemplateMenu" not in ui:
    ui = ui.replace('import androidx.compose.material.icons.filled.Send', 'import androidx.compose.material.icons.filled.Send\nimport androidx.compose.material.icons.filled.List')
    ui = ui.replace(input_row_old, input_row_new)
    # Pass viewModel to ChatScreen
    ui = ui.replace('ChatScreen(\n            thread = state.currentThread!!,', 'ChatScreen(\n            viewModel = viewModel,\n            thread = state.currentThread!!,')
    ui = ui.replace('fun ChatScreen(', 'fun ChatScreen(\n    viewModel: WhatsAppViewModel,')

# Update New Chat Dialog to have a dropdown
dialog_old = """var phone by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showNewChatDialog = false },
            title = { Text("New Conversation") },
            text = {
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    placeholder = { Text("e.g. 919999999999") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.startNewChat(phone)
                    showNewChatDialog = false
                }) {"""
dialog_new = """var phone by remember { mutableStateOf("") }
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
                }) {"""
if "var selectedTemplate" not in ui:
    ui = ui.replace(dialog_old, dialog_new)

with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/ui/erp/WhatsAppInboxScreen.kt', 'w') as f:
    f.write(ui)

print("UI Patched")
