importScripts('https://www.gstatic.com/firebasejs/10.8.1/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/10.8.1/firebase-messaging-compat.js');

const firebaseConfig = {
  apiKey: "AIzaSyCCyYixL_0qBKTB4PoBCZSU7Y2reubCJHQ",
  authDomain: "northend-admin-app.firebaseapp.com",
  projectId: "northend-admin-app",
  storageBucket: "northend-admin-app.firebasestorage.app",
  messagingSenderId: "1020345802825",
  appId: "1:1020345802825:web:9e157578b9a7917c1c1f98",
  measurementId: "G-D79GYH0YCK"
};

firebase.initializeApp(firebaseConfig);

const messaging = firebase.messaging();

messaging.onBackgroundMessage((payload) => {
  console.log('[firebase-messaging-sw.js] Received background message ', payload);
  const notificationTitle = payload.notification.title || payload.data.title || 'Unacademy Admin';
  const notificationOptions = {
    body: payload.notification.body || payload.data.body || '',
    icon: '/icons/icon-192.png',
    data: payload.data
  };

  self.registration.showNotification(notificationTitle, notificationOptions);
});

self.addEventListener('notificationclick', function(event) {
  event.notification.close();
  const targetPath = event.notification.data?.target_path || '/admin';
  
  event.waitUntil(
    clients.matchAll({ type: 'window' }).then((clientList) => {
      for (const client of clientList) {
        if (client.url.includes(self.registration.scope) && 'focus' in client) {
          client.navigate(targetPath);
          return client.focus();
        }
      }
      if (clients.openWindow) {
        return clients.openWindow(targetPath);
      }
    })
  );
});

const CACHE_NAME = "northend-static-v2";
const RUNTIME_CACHE = "northend-runtime-v2";
const PRECACHE_URLS = [
  "/",
  "/index.html",
  "/login",
  "/manifest.json",
  "/icons/icon-192.png",
  "/icons/icon-512.png",
  "/icons/icon-maskable-512.png",
];

self.addEventListener("install", (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => cache.addAll(PRECACHE_URLS))
  );
  self.skipWaiting();
});

self.addEventListener("activate", (event) => {
  event.waitUntil(
    caches.keys().then((keys) =>
      Promise.all(
        keys
          .filter((key) => key !== CACHE_NAME && key !== RUNTIME_CACHE)
          .map((key) => caches.delete(key))
      )
    )
  );
  self.clients.claim();
});

self.addEventListener("fetch", (event) => {
  const { request } = event;
  const url = new URL(request.url);

  if (request.method !== "GET") {
    return;
  }

  if (
    url.pathname.startsWith("/api/") ||
    url.pathname.startsWith("/erp/stream") ||
    url.pathname.startsWith("/push/")
  ) {
    return;
  }

  const isNavigation =
    request.mode === "navigate" ||
    (request.headers.get("accept") && request.headers.get("accept").includes("text/html"));

  if (isNavigation) {
    event.respondWith(
      fetch(request)
        .then(async (response) => {
          if (!response || response.status === 404) {
            const cached =
              (await caches.match("/index.html")) || (await caches.match("/"));
            if (cached) return cached;
          }
          return response;
        })
        .catch(async () => {
          const cached =
            (await caches.match("/index.html")) || (await caches.match("/"));
          return (
            cached ||
            new Response(
              "<!DOCTYPE html><html><head><meta http-equiv='refresh' content='0;url=/'></head><body>Redirecting to application...</body></html>",
              {
                status: 200,
                headers: { "Content-Type": "text/html" },
              }
            )
          );
        })
    );
    return;
  }

  if (url.origin === self.location.origin) {
    event.respondWith(
      caches.match(request).then((cached) => {
        if (cached) return cached;
        return fetch(request)
          .then((response) => {
            if (response && response.status === 200) {
              const clone = response.clone();
              caches.open(RUNTIME_CACHE).then((cache) => cache.put(request, clone).catch(() => {}));
            }
            return response;
          })
          .catch(() => {
            return new Response("Asset unavailable offline", {
              status: 404,
              headers: { "Content-Type": "text/plain" },
            });
          })
      })
    );
    return;
  }
});
