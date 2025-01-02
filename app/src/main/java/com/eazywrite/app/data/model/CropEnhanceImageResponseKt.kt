package com.eazywrite.app.data.model
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CropEnhanceImageResponseKt(
    @Json(name = "code")
    val code: Int,
    @Json(name = "duration")
    val duration: Int,
    @Json(name = "message")
    val message: String,
    @Json(name = "result")
    val result: CropEnhanceResult,
    @Json(name = "version")
    val version: String
)

@JsonClass(generateAdapter = true)
data class CropEnhanceResult(
    @Json(name = "image_list")
    val imageList: List<Image> = emptyList(),
    @Json(name = "origin_height")
    val originHeight: Int,
    @Json(name = "origin_width")
    val originWidth: Int
)

@JsonClass(generateAdapter = true)
data class Image(
    @Json(name = "angle")
    val angle: Int,
    @Json(name = "cropped_height")
    val croppedHeight: Int,
    @Json(name = "cropped_width")
    val croppedWidth: Int,
    @Json(name = "image")
    val image: String,
    @Json(name = "position")
    val position: List<Int>
)