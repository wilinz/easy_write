package com.eazywrite.app.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

const val Gpt3Dot5model = "gpt-3.5-turbo"

@JsonClass(generateAdapter = true)
data class ChatBody(
    @Json(name = "messages")
    val messages: List<Message>,
    @Json(name = "model")
    val model: String = Gpt3Dot5model,
    @Json(name = "stream")
    var stream: Boolean = false,
)

@JsonClass(generateAdapter = true)
data class Message(
    @Json(ignore = true)
    val id: String? = null,
    @Json(name = "content")
    var content: String,
    @Json(name = "role")
    val role: String = RoleUser
) {
    companion object {
        const val RoleUser = "user"
        const val RoleSystem = "system"
        const val RoleAssistant = "assistant"
    }
}