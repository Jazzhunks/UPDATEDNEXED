import re

with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/data/remote/models/WhatsAppModels.kt', 'r') as f:
    models = f.read()

models = models.replace('val kind: String,', '@Json(name = "type") val kind: String,')
models = models.replace('val timestamp: String', '@Json(name = "created_at") val timestamp: String')

with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/data/remote/models/WhatsAppModels.kt', 'w') as f:
    f.write(models)
print("Models patched")
