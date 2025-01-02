package com.eazywrite.app.data.network.service

import com.eazywrite.app.data.model.AppVersion
import retrofit2.http.GET
import retrofit2.http.Query

interface AppVersionService {
    @GET("/app_version")
    suspend fun getAppVersion(@Query("appid") appid: String = "com.eazywrite.app"): AppVersion
}