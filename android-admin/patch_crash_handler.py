with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/ui/MainActivity.kt', 'r') as f:
    main = f.read()

crash_code = """
        val prefs = getSharedPreferences("crash_prefs", android.content.Context.MODE_PRIVATE)
        val lastCrash = prefs.getString("last_crash", null)
        if (lastCrash != null) {
            prefs.edit().remove("last_crash").apply()
            // We can pass this to a state and show a dialog, but for now let's just show an alert dialog.
            android.app.AlertDialog.Builder(this)
                .setTitle("Crash Log")
                .setMessage(lastCrash)
                .setPositiveButton("OK", null)
                .show()
        }

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            val sw = java.io.StringWriter()
            exception.printStackTrace(java.io.PrintWriter(sw))
            getSharedPreferences("crash_prefs", android.content.Context.MODE_PRIVATE)
                .edit()
                .putString("last_crash", sw.toString())
                .commit()
            defaultHandler?.uncaughtException(thread, exception)
        }
"""

main = main.replace('WindowCompat.setDecorFitsSystemWindows(window, false)', 'WindowCompat.setDecorFitsSystemWindows(window, false)\n' + crash_code)

with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/ui/MainActivity.kt', 'w') as f:
    f.write(main)
