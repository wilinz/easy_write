@file:OptIn(BetaOpenAI::class)

package com.eazywrite.app.ui.gpt

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.eazywrite.app.common.toast
import com.eazywrite.app.data.maxChatContextSize
import com.eazywrite.app.data.model.ChatBody
import com.eazywrite.app.data.model.Gpt3Dot5model
import com.eazywrite.app.data.repository.OpenaiRepository
import io.ktor.serialization.JsonConvertException
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@OptIn(BetaOpenAI::class)
val welcomeMessage = ChatMessage(
    role = ChatRole.System,
    content = "你好！有什么需要我帮忙的吗？\n作为AI，我可以回答一些你关心的问题，帮助你解决问题，提供科学和历史知识，甚至玩一些有趣的游戏。"
)


class ChatViewModel(application: Application) : AndroidViewModel(application) {


    val messages = mutableStateListOf(
        welcomeMessage
    )

    private val _maxContextNumber = MutableStateFlow(10)

    val maxContextNumber get() = _maxContextNumber.asStateFlow()

    init {
        viewModelScope.launch {
            getApplication<Application>().maxChatContextSize.collect {
                _maxContextNumber.value = it
            }
        }
    }

    fun getSubMessageList(max: Int): MutableList<ChatMessage> {
        return messages.subList(maxOf(messages.size - max, 0), messages.size)
    }

    var inputMessage by mutableStateOf("")

    var isDisabledDeleteMessage by mutableStateOf(false)

    suspend fun chat() = kotlin.runCatching {
        isDisabledDeleteMessage = true
        val msg0 = ChatMessage(
            role = ChatRole.User,
            content = inputMessage
        )
        messages.add(msg0)
        inputMessage = ""
        val msg1 = ChatMessage(
            role = ChatRole.Assistant,
            content = ""
        )
        messages.add(msg1)
        val index = messages.indexOf(msg1)

        OpenaiRepository.chatStream(
            ChatCompletionRequest(
                model = ModelId(Gpt3Dot5model),
                messages = getSubMessageList(maxContextNumber.value)
            )
        ).catch {
            when (it) {
                is IOException -> {
                    toast(text = "连接服务器失败：${it.message}")
                }
                else -> {
                    if (it !is JsonConvertException) {
                        it.printStackTrace()
                        toast(text = "出错了：${it.message}")
                    }
                }
            }
            messages.removeAt(index)
        }.collect {
            kotlin.runCatching {
                val old = messages[index]
                messages[index] =
                    old.copy(content = old.content + (it.choices[0].delta?.content ?: ""))
            }
        }
        isDisabledDeleteMessage = false
    }.onFailure {
        toast(text = "出错了：${it.message}")
        it.printStackTrace()
    }

    fun chatStream(chatBody: ChatBody) {
//        viewModelScope.launch {
//            OpenaiRepository.chatStream(chatBody)
//                .catch {
//                    if (it is IOException) {
//                        toast(text = "连接服务器失败：${it.message}")
//                    } else {
//                        toast(text = "出错了：${it.message}")
//                    }
//                }
//                .collect { content ->
//                    if (content == null) return@collect
//                    val id = content.id
//                    val choice = content.choices.firstOrNull()
//                    val msg = choice?.delta?.content ?: ""
//                    val index = messages.indexOfFirst { it.id == id }
//                    val localMsg = messages.getOrNull(index)
//                    if (localMsg == null) {
//                        messages.add(Message(id = id, content = msg, role = Message.RoleAssistant))
//                    } else {
//                        messages[index] =
//                            localMsg.copy(content = localMsg.content + msg)
//                    }
//                }
//        }
    }

}