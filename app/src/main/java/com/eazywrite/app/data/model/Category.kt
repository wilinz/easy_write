package com.eazywrite.app.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Categories(
    @Json(name = "in")
    val inList: List<String>,
    @Json(name = "out")
    val outList: List<String>,
)