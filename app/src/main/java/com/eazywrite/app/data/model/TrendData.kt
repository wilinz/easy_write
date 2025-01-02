package com.eazywrite.app.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class FinancialAnalysisData(
    @Json(name = "in_total")
    val inTotal: BigDecimal,
    @Json(name = "out_total")
    val outTotal: BigDecimal,
    @Json(name = "in_data")
    val inData: List<TrendData>,
    @Json(name = "out_data")
    val outData: List<TrendData>
)

@JsonClass(generateAdapter = true)
data class TrendData(
    val date: String,
    val amount: BigDecimal
)