with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/data/remote/models/WhatsAppModels.kt', 'a') as f:
    f.write("\n\ndata class WhatsAppTemplate(val name: String, val language: String, val category: String? = null)\n")
    f.write("data class WhatsAppTemplateListResponse(val data: List<WhatsAppTemplate>)\n")

with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/data/remote/AdminApiService.kt', 'r') as f:
    api = f.read()

if "getWhatsAppTemplates" not in api:
    api = api.replace('suspend fun sendDirectWhatsApp', '@GET("whatsapp/templates")\n    suspend fun getWhatsAppTemplates(): Response<com.northend.admin.data.remote.models.WhatsAppTemplateListResponse>\n\n    suspend fun sendDirectWhatsApp')
    with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/data/remote/AdminApiService.kt', 'w') as f:
        f.write(api)

with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/data/repository/AdminRepository.kt', 'r') as f:
    repo = f.read()

repo_method = """
    suspend fun getWhatsAppTemplates(): ResultWrapper<List<com.northend.admin.data.remote.models.WhatsAppTemplate>> {
        return safeApiCall { apiService.getWhatsAppTemplates() }.let {
            when (it) {
                is ResultWrapper.Success -> ResultWrapper.Success(it.data.data)
                is ResultWrapper.Error -> it
                else -> ResultWrapper.Error("Unknown error")
            }
        }
    }
"""
if "getWhatsAppTemplates" not in repo:
    repo = repo.replace('suspend fun sendDirectWhatsApp', repo_method + '\n    suspend fun sendDirectWhatsApp')
    with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/data/repository/AdminRepository.kt', 'w') as f:
        f.write(repo)
