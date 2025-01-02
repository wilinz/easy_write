package com.eazywrite.app.data.repository

import TencentAudioRequestData
import com.eazywrite.app.data.network.Network

object AudioRepository {

    suspend fun audioToText(requestData: TencentAudioRequestData) =
        Network.tencentCloudAudioService.sentenceRecognition(requestData)


}