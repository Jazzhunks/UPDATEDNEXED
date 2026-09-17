with open('/Users/mudasirmushtaq/Documents/app/northend/frontend/src/lib/firebase.js', 'r') as f:
    code = f.read()

# Change to compat imports to avoid Webpack 5 ESM resolution bugs on older react-scripts
new_code = code.replace(
    'import { initializeApp } from "firebase/app";',
    'import firebase from "firebase/compat/app";'
).replace(
    'import { getMessaging, getToken, onMessage } from "firebase/messaging";',
    'import "firebase/compat/messaging";'
).replace(
    'const app = initializeApp(firebaseConfig);',
    'firebase.initializeApp(firebaseConfig);\nconst app = firebase.app();'
).replace(
    'getMessaging(app)',
    'firebase.messaging()'
).replace(
    'getToken(messaging, { vapidKey:',
    'messaging.getToken({ vapidKey:'
)

with open('/Users/mudasirmushtaq/Documents/app/northend/frontend/src/lib/firebase.js', 'w') as f:
    f.write(new_code)
