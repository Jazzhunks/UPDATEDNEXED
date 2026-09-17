with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/data/remote/models/WhatsAppModels.kt', 'r') as f:
    models = f.read()

models = models.replace('val direction: String,', 'val direction: String? = "inbound",')
models = models.replace('@Json(name = "type") val kind: String,', '@Json(name = "type") val kind: String? = "text",')
models = models.replace('@Json(name = "created_at") val timestamp: String', '@Json(name = "created_at") val timestamp: String? = null')
models = models.replace('@Json(name = "thread_id") val threadId: String', '@Json(name = "thread_id") val threadId: String? = null')

with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/data/remote/models/WhatsAppModels.kt', 'w') as f:
    f.write(models)
print("Models bulletproofed")
