package com.eazywrite.app.data.model
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BillsRecognitionResponse(
    @Json(name = "code")
    val code: Int,
    @Json(name = "message")
    val message: String,
    @Json(name = "result")
    val result: BillsCropResult
)

@JsonClass(generateAdapter = true)
data class BillsCropResult(
    @Json(name = "object_list")
    val objectList: List<Object>
)

@JsonClass(generateAdapter = true)
data class Object(
    @Json(name = "class")
    val classX: String,
    @Json(name = "image_angle")
    val imageAngle: Int,
    @Json(name = "item_list")
    val itemList: List<Item>,
    @Json(name = "position")
    val position: List<Int>,
    @Json(name = "rotated_image_height")
    val rotatedImageHeight: Int,
    @Json(name = "rotated_image_width")
    val rotatedImageWidth: Int,
    @Json(name = "type")
    val type: String,
    @Json(name = "type_description")
    val typeDescription: String
)

@JsonClass(generateAdapter = true)
data class Item(
    @Json(name = "description")
    val description: String,
    @Json(name = "key")
    val key: String,
    @Json(name = "position")
    val position: List<Int>,
    @Json(name = "value")
    val value: String
)