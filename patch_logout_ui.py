with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/ui/erp/WhatsAppInboxScreen.kt', 'r') as f:
    ui = f.read()

# Modify function signature
ui = ui.replace('fun WhatsAppInboxScreen(viewModel: WhatsAppViewModel = hiltViewModel()) {', 'fun WhatsAppInboxScreen(viewModel: WhatsAppViewModel = hiltViewModel(), onLogout: () -> Unit = {}) {')

# Add Logout Icon to Thread List TopAppBar
old_top_bar = """TopAppBar(
                    title = { Text("WhatsApp", color = Color.White, fontWeight = FontWeight.SemiBold) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = WhatsAppTeal)
                )"""

new_top_bar = """TopAppBar(
                    title = { Text("WhatsApp", color = Color.White, fontWeight = FontWeight.SemiBold) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = WhatsAppTeal),
                    actions = {
                        IconButton(onClick = onLogout) {
                            Icon(androidx.compose.material.icons.Icons.Filled.ExitToApp, "Logout", tint = Color.White)
                        }
                    }
                )"""

ui = ui.replace(old_top_bar, new_top_bar)

if "import androidx.compose.material.icons.filled.ExitToApp" not in ui:
    ui = ui.replace('import androidx.compose.material.icons.filled.Message', 'import androidx.compose.material.icons.filled.Message\nimport androidx.compose.material.icons.filled.ExitToApp')

with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/ui/erp/WhatsAppInboxScreen.kt', 'w') as f:
    f.write(ui)
print("WhatsApp UI patched for logout")
