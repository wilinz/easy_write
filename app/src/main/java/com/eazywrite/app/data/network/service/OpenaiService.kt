package com.eazywrite.app.data.network.service

import com.eazywrite.app.data.model.ChatBody
import com.eazywrite.app.data.model.ChatResponse
import com.eazywrite.app.data.model.Transcriptions
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenaiService {
    @POST("v1/chat/completions")
    suspend fun chat(@Body chatBody: ChatBody): ChatResponse

    @POST("v1/audio/transcriptions")
    suspend fun transcriptions(
        @Body multipartBody: MultipartBody,
        @Header("Content-Type") contentType: String = "multipart/form-data; boundary=WebAppBoundary"
    ): Transcriptions
}