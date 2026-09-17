"""Firebase Cloud Messaging client."""

import asyncio
import logging
import os
from typing import Any, Dict, List, Optional

import firebase_admin
from firebase_admin import credentials, messaging

logger = logging.getLogger("fcm")

_fcm_app = None


def init_fcm():
    global _fcm_app
    if _fcm_app is not None:
        return _fcm_app

    service_account_path = os.getenv("FIREBASE_SERVICE_ACCOUNT_KEY")
    project_id = os.getenv("FIREBASE_PROJECT_ID", "northend-admin-app")

    if not service_account_path:
        logger.warning("FIREBASE_SERVICE_ACCOUNT_KEY not configured; FCM disabled")
        return None

    try:
        cred = credentials.Certificate(service_account_path)
        _fcm_app = firebase_admin.initialize_app(cred, name="fcm")
        return _fcm_app
    except Exception as e:
        logger.error("Failed to initialize FCM: %s", e)
        return None


def _send_sync(app, message: messaging.Message) -> Dict[str, Any]:
    try:
        response = messaging.send(message, app=app)
        return {"message_id": response}
    except messaging.UnregisteredError:
        return {"error": "unregistered"}
    except Exception as e:
        return {"error": str(e)}


def _send_each_sync(app, messages: List[messaging.Message]) -> Any:
    try:
        return messaging.send_each(messages, app=app)
    except Exception as e:
        logger.error("FCM send_each failed: %s", e)
        raise


async def send_fcm_to_token(
    token: str,
    title: str,
    body: str,
    *,
    data: Optional[Dict[str, Any]] = None,
    url: Optional[str] = None,
) -> Dict[str, Any]:
    app = init_fcm()
    if app is None:
        return {"skipped": True}

    notification = messaging.Notification(title=title, body=body)
    android = messaging.AndroidConfig(
        notification=messaging.AndroidNotification(
            priority="high",
            default_sound=True,
            default_vibrate_timings=True,
        ),
    )
    apns = messaging.APNSConfig(
        payload=messaging.APNSPayload(
            aps=messaging.Aps(sound="default", badge=1),
        ),
    )

    message = messaging.Message(
        notification=notification,
        data=data or {},
        token=token,
        android=android,
        apns=apns,
    )
    if url:
        message.webpush = messaging.WebpushConfig(
            fcm_options=messaging.WebpushFCMOptions(link=url)
        )

    return await asyncio.to_thread(_send_sync, app, message)


async def send_fcm_to_topic(
    topic: str,
    title: str,
    body: str,
    *,
    data: Optional[Dict[str, Any]] = None,
    url: Optional[str] = None,
) -> Dict[str, Any]:
    app = init_fcm()
    if app is None:
        return {"skipped": True}

    notification = messaging.Notification(title=title, body=body)
    message = messaging.Message(
        notification=notification,
        data=data or {},
        topic=topic,
    )
    if url:
        message.webpush = messaging.WebpushConfig(
            fcm_options=messaging.WebpushFCMOptions(link=url)
        )

    return await asyncio.to_thread(_send_sync, app, message)


async def send_fcm_to_tokens(
    tokens: List[str],
    title: str,
    body: str,
    *,
    data: Optional[Dict[str, Any]] = None,
    url: Optional[str] = None,
) -> Dict[str, Any]:
    app = init_fcm()
    if app is None or not tokens:
        return {"skipped": True, "reason": "no_tokens" if not tokens else "fcm_disabled"}

    batch_size = 500
    total_success = 0
    total_failed = 0

    for i in range(0, len(tokens), batch_size):
        batch = tokens[i:i + batch_size]
        notification = messaging.Notification(title=title, body=body)
        android = messaging.AndroidConfig(
            notification=messaging.AndroidNotification(
                priority="high",
                default_sound=True,
                default_vibrate_timings=True,
            ),
        )

        messages = []
        for token in batch:
            message = messaging.Message(
                notification=notification,
                data=data or {},
                token=token,
                android=android,
            )
            messages.append(message)

        try:
            batch_response = await asyncio.to_thread(_send_each_sync, app, messages)
            for resp in batch_response.responses:
                if resp.success:
                    total_success += 1
                else:
                    total_failed += 1
        except Exception as e:
            logger.error("FCM batch send failed: %s", e)
            total_failed += len(batch)

    return {
        "total": len(tokens),
        "success": total_success,
        "failed": total_failed,
    }
