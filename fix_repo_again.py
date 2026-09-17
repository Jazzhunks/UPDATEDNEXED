import re

with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/data/repository/AdminRepository.kt', 'r') as f:
    repo = f.read()

repo = repo.replace('is ResultWrapper.Success -> ResultWrapper.Success(it.data.data)', 'is ResultWrapper.Success<*> -> ResultWrapper.Success((it.data as com.northend.admin.data.remote.models.WhatsAppTemplateListResponse).data)')

with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/data/repository/AdminRepository.kt', 'w') as f:
    f.write(repo)
print("Repo fixed")
