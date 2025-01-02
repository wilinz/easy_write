package com.eazywrite.app.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal
import java.time.LocalDate

@JsonClass(generateAdapter = true)
data class PercentageData(
    @Json(name = "date_range")
    val dateRange: DateRange,
    @Json(name = "in_total")
    val inTotal: BigDecimal,
    @Json(name = "out_total")
    val outTotal: BigDecimal,
    @Json(name = "in_data")
    val inData: List<AnalyseData>,
    @Json(name = "out_data")
    val outData: List<AnalyseData>,
)

@JsonClass(generateAdapter = true)
data class DateRange(val start: LocalDate, val end: LocalDate)

@JsonClass(generateAdapter = true)
data class AnalyseData(
    val category: String,
    val amount: BigDecimal,
    val percentage: Double,
)