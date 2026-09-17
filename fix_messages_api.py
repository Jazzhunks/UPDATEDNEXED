with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/data/remote/models/WhatsAppModels.kt', 'a') as f:
    f.write("\n\ndata class WhatsAppMessagesResponse(val thread: WhatsAppThread?, val contact: Any?, val items: List<WhatsAppMessage>)\n")

with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/data/remote/AdminApiService.kt', 'r') as f:
    api = f.read()

api = api.replace('Response<List<com.northend.admin.data.remote.models.WhatsAppMessage>>', 'Response<com.northend.admin.data.remote.models.WhatsAppMessagesResponse>')

with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/data/remote/AdminApiService.kt', 'w') as f:
    f.write(api)

with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/data/repository/AdminRepository.kt', 'r') as f:
    repo = f.read()

# In AdminRepository, it returns ResultWrapper<List<WhatsAppMessage>>. We need to unwrap it.
# Find suspend fun getWhatsAppMessages
repo = repo.replace('return safeApiCall { apiService.getWhatsAppMessages(threadId, limit) }', 'return safeApiCall { apiService.getWhatsAppMessages(threadId, limit) }.let {\n            when (it) {\n                is ResultWrapper.Success<*> -> ResultWrapper.Success((it.data as com.northend.admin.data.remote.models.WhatsAppMessagesResponse).items)\n                is ResultWrapper.Error -> it\n                else -> ResultWrapper.Error("Unknown error")\n            }\n        }')

with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/data/repository/AdminRepository.kt', 'w') as f:
    f.write(repo)

print("API patched")
