with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/ui/AppNavHost.kt', 'r') as f:
    nav = f.read()

# Replace ErpScreen with WhatsAppInboxScreen in the "erp" route
old_erp = """composable("erp") {
            val dashboardViewModel: DashboardViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            val user by dashboardViewModel.user.collectAsStateWithLifecycle()
            val currentUser = user ?: User(id = "", name = "", email = "", role = "attendance")
            ErpScreen(
                user = currentUser,
                onLogout = {
                    tokenManager.clearTokens()
                    navController.navigate("login") {
                        popUpTo("erp") { inclusive = true }
                    }
                }
            )
        }"""

new_erp = """composable("erp") {
            com.northend.admin.ui.erp.WhatsAppInboxScreen(
                onLogout = {
                    tokenManager.clearTokens()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }"""

nav = nav.replace(old_erp, new_erp)
if "import com.northend.admin.ui.erp.WhatsAppInboxScreen" not in nav:
    nav = nav.replace('import com.northend.admin.ui.erp.ErpScreen', 'import com.northend.admin.ui.erp.ErpScreen\nimport com.northend.admin.ui.erp.WhatsAppInboxScreen')

with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/ui/AppNavHost.kt', 'w') as f:
    f.write(nav)
print("AppNavHost patched")
