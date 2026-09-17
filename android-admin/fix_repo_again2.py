import re

with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/data/repository/AdminRepository.kt', 'r') as f:
    repo = f.read()

old_func = """    suspend fun getWhatsAppMessages(threadId: String): ResultWrapper<List<com.northend.admin.data.remote.models.WhatsAppMessage>> =
        safeApiCall { apiService.getWhatsAppMessages(threadId).body()!! }"""

new_func = """    suspend fun getWhatsAppMessages(threadId: String): ResultWrapper<List<com.northend.admin.data.remote.models.WhatsAppMessage>> {
        return safeApiCall { apiService.getWhatsAppMessages(threadId) }.let {
            when (it) {
                is ResultWrapper.Success<*> -> ResultWrapper.Success((it.data as com.northend.admin.data.remote.models.WhatsAppMessagesResponse).items)
                is ResultWrapper.Error -> it
                else -> ResultWrapper.Error("Unknown error")
            }
        }
    }"""

repo = repo.replace(old_func, new_func)

with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/data/repository/AdminRepository.kt', 'w') as f:
    f.write(repo)
