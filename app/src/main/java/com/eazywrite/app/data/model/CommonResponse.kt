package com.eazywrite.app.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CommonResponse(
    @Json(name = "code")
    val code: Int,
    @Json(name = "data")
    val `data`: Any?,
    @Json(name = "msg")
    val msg: String
)