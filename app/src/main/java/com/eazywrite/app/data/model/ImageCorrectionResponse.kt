package com.eazywrite.app.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImageCorrectionResponse(
    val code: Int,
    val duration: Int,
    val message: String,
    val result: Result,
    val version: String
)

@JsonClass(generateAdapter = true)
data class Result(
    val image: String
)