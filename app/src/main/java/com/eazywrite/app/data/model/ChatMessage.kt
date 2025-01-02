package com.eazywrite.app.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatMessage(
    val msg: String,
    val user: String
)