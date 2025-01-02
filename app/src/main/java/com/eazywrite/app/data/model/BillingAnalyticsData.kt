package com.eazywrite.app.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BillingAnalyticsData(
    @Json(name = "percentage_data")
    val percentageData: PercentageData,
    @Json(name = "trend_data")
    val trendData: FinancialAnalysisData
)