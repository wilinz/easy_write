package com.eazywrite.app.data.model

import com.squareup.moshi.JsonClass
import java.math.BigDecimal
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class ImportBillData(
    var datetime: LocalDateTime,
    var category: String,
    var transactionPartner: String,
    var partnerAccount: String = "",
    var name: String,
    var type: String,
    var amount: BigDecimal,
    var paymentMethod: String,
    var status: String,
    var transactionNo: String,
    var shopNo: String,
    var comment: String
)