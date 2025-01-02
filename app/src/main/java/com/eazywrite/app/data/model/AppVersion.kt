package com.eazywrite.app.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime


@JsonClass(generateAdapter = true)
data class AppVersion(
    @Json(name = "code")
    val code: Int,
    @Json(name = "data")
    val `data`: AppVersionData,
    @Json(name = "msg")
    val msg: String
)

@JsonClass(generateAdapter = true)
data class AppVersionData(
    @Json(name = "app_name")
    val appName: String,
    @Json(name = "appid")
    val appid: String,
    @Json(name = "can_hide")
    val canHide: Boolean,
    @Json(name = "changelog")
    val changelog: String,
    @Json(name = "created_at")
    val createdAt: LocalDateTime,
    @Json(name = "download_url")
    val downloadUrl: String,
    @Json(name = "is_force")
    val isForce: Boolean,
    @Json(name = "updated_at")
    val updatedAt: LocalDateTime,
    @Json(name = "version_code")
    val versionCode: Long,
    @Json(name = "version_name")
    val versionName: String
)