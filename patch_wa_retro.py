import re

with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/data/remote/models/WhatsAppModels.kt', 'a') as f:
    f.write("\ndata class SendDirectRequest(val phone: String, val template_name: String, val template_language: String = \"en\")\n")

with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/data/remote/AdminApiService.kt', 'r') as f:
    api = f.read()

direct_post = """
    @POST("whatsapp/send-direct")
    suspend fun sendDirectWhatsApp(@Body req: SendDirectRequest): Response<Any>
"""
if "sendDirectWhatsApp" not in api:
    api = api.replace('suspend fun sendWhatsAppMessage', direct_post + '\n    @POST("whatsapp/threads/{thread_id}/messages")\n    suspend fun sendWhatsAppMessage')
    with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/data/remote/AdminApiService.kt', 'w') as f:
        f.write(api)

with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/data/repository/AdminRepository.kt', 'r') as f:
    repo = f.read()

repo_method = """
    suspend fun sendDirectWhatsApp(phone: String, templateName: String): ResultWrapper<Any> {
        return safeApiCall { apiService.sendDirectWhatsApp(com.northend.admin.data.remote.models.SendDirectRequest(phone, templateName)) }
    }
"""
if "sendDirectWhatsApp" not in repo:
    repo = repo.replace('suspend fun sendWhatsAppMessage', repo_method + '\n    suspend fun sendWhatsAppMessage')
    with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/data/repository/AdminRepository.kt', 'w') as f:
        f.write(repo)
        
print("Retrofit patched")
