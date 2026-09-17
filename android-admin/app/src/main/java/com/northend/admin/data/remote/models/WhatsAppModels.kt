package com.northend.admin.data.remote.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class WhatsAppThread(
    val id: String,
    @Json(name = "wa_id") val phone: String? = null,
    @Json(name = "profile_name") val contactName: String? = null,
    @Json(name = "linked_name") val studentName: String? = null,
    @Json(name = "last_message_preview") val lastMessagePreview: String? = null,
    @Json(name = "last_message_at") val lastMessageAt: String? = null,
    @Json(name = "unread_count") val unreadCount: Int = 0,
    val tags: List<String> = emptyList()
)

data class WhatsAppMessage(
    val id: String,
    @Json(name = "thread_id") val threadId: String? = null,
    val direction: String? = "inbound", // "inbound" or "outbound"
    @Json(name = "type") val kind: String? = "text", // "text", "template", "image", etc.
    val text: String? = null,
    val status: String? = null, // "sent", "delivered", "read", "failed"
    @Json(name = "created_at") val timestamp: String? = null
)

data class WhatsAppSendMessageRequest(
    val kind: String = "text",
    val text: String
)

data class SendDirectRequest(val phone: String, val template_name: String, val template_language: String = "en")


data class WhatsAppTemplate(val name: String, val language: String, val category: String? = null)
data class WhatsAppTemplateListResponse(val data: List<WhatsAppTemplate>)


data class WhatsAppMessagesResponse(val thread: WhatsAppThread?, val items: List<WhatsAppMessage>)
