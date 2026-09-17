with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/ui/MainActivity.kt', 'r') as f:
    main = f.read()

main = main.replace('WhatsAppInboxScreen()', 'AppNavHost()')

with open('/Users/mudasirmushtaq/Documents/app/northend/android-admin/app/src/main/java/com/northend/admin/ui/MainActivity.kt', 'w') as f:
    f.write(main)
print("MainActivity patched")
