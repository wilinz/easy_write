package com.eazywrite.app.data.network

import android.util.Log
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 网络请求拦截器
 */
class TextinApiRequestInterceptor(private val textinAppId: String, private val textinSecretCode: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        val httpUrl: HttpUrl = chain.request().url
        val urlString = httpUrl.toString()
        Log.d("RequestInterceptor", "request to url:$urlString");

        //加上请求头参数
        builder.addHeader("x-ti-app-id", textinAppId) // TODO
        builder.addHeader("x-ti-secret-code", textinAppId) // TODO
        if (urlString.endsWith("ai/service/v1/crop_enhance_image") ||  //图像切边增强、裁切图像主体区域并增强api
            urlString.endsWith("/robot/v1.0/api/bills_crop") ||  //国内通用票据识别
            urlString.endsWith("/ai/service/v1/dewarp")
        ) { //图像切边矫正
            builder.addHeader("connection", "Keep-Alive")
            builder.addHeader("Content-Type", "application/octet-stream")
        }
        return chain.proceed(builder.build())
    }
}