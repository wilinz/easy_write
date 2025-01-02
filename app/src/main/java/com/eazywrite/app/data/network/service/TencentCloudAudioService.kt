package com.eazywrite.app.data.network.service

import TencentAudioRequestData
import TencentAudioResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface TencentCloudAudioService {
    @POST("/tencent/sentence_recognition")
    suspend fun sentenceRecognition(@Body requestData: TencentAudioRequestData): TencentAudioResponse
}