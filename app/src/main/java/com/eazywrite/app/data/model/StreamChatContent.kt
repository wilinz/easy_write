package com.eazywrite.app.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class StreamChatContent(
    @Json(name = "choices")
    val choices: List<StreamChatChoice>,
    @Json(name = "created")
    val created: Int,
    @Json(name = "id")
    val id: String,
    @Json(name = "model")
    val model: String,
    @Json(name = "object")
    val objectX: String
)

@JsonClass(generateAdapter = true)
data class StreamChatChoice(
    @Json(name = "delta")
    val delta: Delta,
    @Json(name = "finish_reason")
    val finishReason: String? = null,
    @Json(name = "index")
    val index: Int = 0
)

@JsonClass(generateAdapter = true)
data class Delta(
    @Json(name = "content")
    val content: String?,
    @Json(name = "role")
    val role: String?
)