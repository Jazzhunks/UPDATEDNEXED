import re

# Fix AdminApiService
with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/data/remote/AdminApiService.kt', 'r') as f:
    api = f.read()

# Remove the stray annotation
api = api.replace('    @POST("whatsapp/threads/{id}/messages")\n    \n    @POST("whatsapp/send-direct")', '    @POST("whatsapp/send-direct")')

with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/data/remote/AdminApiService.kt', 'w') as f:
    f.write(api)

# Fix WhatsAppInboxScreen
with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/ui/erp/WhatsAppInboxScreen.kt', 'r') as f:
    ui = f.read()

ui = ui.replace('msg.textBody', 'msg.text')

with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/ui/erp/WhatsAppInboxScreen.kt', 'w') as f:
    f.write(ui)

print("Fixed")
