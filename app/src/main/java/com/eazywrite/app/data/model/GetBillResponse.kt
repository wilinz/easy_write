package com.eazywrite.app.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class GetBillResponse(
    @Json(name = "code")
    val code: Int,
    @Json(name = "data")
    val `data`: List<Bill>?,
    @Json(name = "msg")
    val msg: String
)

@JsonClass(generateAdapter = true)
data class PostAndPutBillResponse(
    @Json(name = "code")
    val code: Int,
    @Json(name = "data")
    val `data`: List<PostAndPutBillResponseData>,
    @Json(name = "msg")
    val msg: String
)

@JsonClass(generateAdapter = true)
data class PostAndPutBillResponseData(
    @Json(name = "id")
    val id: Long,
    @Json(name = "third_party_id")
    val thirdPartyID: String,
    @Json(name = "images_comment")
    val imagesComment: StringList
)

@JsonClass(generateAdapter = true)
data class DeleteResponse(
    @Json(name = "code")
    val code: Int,
    @Json(name = "data")
    val `data`: Any?,
    @Json(name = "msg")
    val msg: String
)