package com.eazywrite.app.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Transcriptions(
    val text: String
)