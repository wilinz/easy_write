package com.eazywrite.app.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BillEditable(
    val amount: String = "",
    val comment: String = "",
    val datetime: String = "",
    val category: String = "",
    val name: String = "",
    val type: String = "",
    @Json(name = "transaction_partner")
    val transactionPartner: String = "",
    @Json(name = "images_comment")
    val imagesComment: StringList? = null
)