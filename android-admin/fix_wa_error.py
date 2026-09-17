with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/ui/erp/WhatsAppInboxScreen.kt', 'r') as f:
    ui = f.read()

# Add a text block to show error
error_code = """
            if (state.error != null) {
                Text(text = "ERROR: ${state.error}", color = Color.Red, modifier = Modifier.padding(16.dp).background(Color.White))
            }
"""
ui = ui.replace('if (state.isLoadingThreads) {', error_code + '\n            if (state.isLoadingThreads) {')

with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/ui/erp/WhatsAppInboxScreen.kt', 'w') as f:
    f.write(ui)
