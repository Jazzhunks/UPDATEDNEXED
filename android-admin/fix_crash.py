with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/data/remote/models/WhatsAppModels.kt', 'r') as f:
    models = f.read()

models = models.replace('val contact: Any?, ', '')

with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/data/remote/models/WhatsAppModels.kt', 'w') as f:
    f.write(models)

with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/data/repository/AdminRepository.kt', 'r') as f:
    repo = f.read()

repo = repo.replace('safeApiCall { apiService.getWhatsAppMessages(threadId) }.let', 'safeApiCall { apiService.getWhatsAppMessages(threadId).body()!! }.let')

with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/data/repository/AdminRepository.kt', 'w') as f:
    f.write(repo)

print("Fixed Crash")
