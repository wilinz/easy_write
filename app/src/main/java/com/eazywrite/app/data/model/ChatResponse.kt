package com.eazywrite.app.data.model
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatResponse(
    @Json(name = "choices")
    val choices: List<Choice>,
    @Json(name = "created")
    val created: Int,
    @Json(name = "id")
    val id: String,
    @Json(name = "model")
    val model: String,
    @Json(name = "object")
    val objectX: String,
    @Json(name = "usage")
    val usage: Usage
)

@JsonClass(generateAdapter = true)
data class Choice(
    @Json(name = "finish_reason")
    val finishReason: String,
    @Json(name = "index")
    val index: Int,
    @Json(name = "message")
    val message: Message
)

@JsonClass(generateAdapter = true)
data class Usage(
    @Json(name = "completion_tokens")
    val completionTokens: Int,
    @Json(name = "prompt_tokens")
    val promptTokens: Int,
    @Json(name = "total_tokens")
    val totalTokens: Int
)