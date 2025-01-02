@file:OptIn(ExperimentalStdlibApi::class, InternalAPI::class, BetaOpenAI::class)

package com.eazywrite.app.data.repository

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.eazywrite.app.data.model.ChatBody
import com.eazywrite.app.data.model.ChatResponse
import com.eazywrite.app.data.model.Transcriptions
import com.eazywrite.app.data.network.Network
import io.ktor.util.InternalAPI
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

object OpenaiRepository {
    suspend fun chat(chatBody: ChatBody) = Network.openaiService.chat(chatBody)

    fun chatStream(request: ChatCompletionRequest): Flow<ChatCompletionChunk> {
        return Network.openAI.chatCompletions(request)
    }

    fun chatAsync(
        chatBody: ChatBody,
        onSuccess: (ChatResponse) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        MainScope().launch {
            kotlin.runCatching {
                Network.openaiService.chat(chatBody)
            }.onSuccess(onSuccess).onFailure(onFailure)
        }
    }

    suspend fun transcriptions(audioFile: File): Transcriptions {
        val multipartBody = MultipartBody.Builder("WebAppBoundary")
            .addFormDataPart("model", "whisper-1")
            .addFormDataPart("response_format", "json")
//            .addFormDataPart("language", "zh")
            .addFormDataPart("file", "openai.mp3", audioFile.asRequestBody())
            .build()
        return Network.openaiService.transcriptions(multipartBody)
    }
}