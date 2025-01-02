@file:OptIn(ExperimentalStdlibApi::class, ExperimentalStdlibApi::class)

package com.eazywrite.app.ui.audiorecord

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.eazywrite.app.data.model.BillEditable
import com.eazywrite.app.data.model.Categories
import com.eazywrite.app.data.model.ChatBody
import com.eazywrite.app.data.model.Message
import com.eazywrite.app.data.moshi
import com.eazywrite.app.data.repository.BillRepository
import com.eazywrite.app.data.repository.CategoryRepository
import com.eazywrite.app.data.repository.OpenaiRepository
import com.eazywrite.app.ui.bill.BillEditableState
import com.eazywrite.app.ui.bill.getEditableState
import com.eazywrite.app.util.createFile
import com.eazywrite.app.util.extractJsonFromString
import com.eazywrite.app.util.getRandomName
import com.squareup.moshi.adapter
import com.zqc.opencc.android.lib.ChineseConverter
import com.zqc.opencc.android.lib.ConversionType
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class AudioRecordViewModel(application: Application) : AndroidViewModel(application) {

    val audioFile by lazy {
        File(
            getApplication<Application>().cacheDir,
            "audio/"+getRandomName() + ".mp3"
        ).apply { createFile() }
    }
    var msg by mutableStateOf("今天中午点外卖花了20")

    suspend fun transcriptions() {
//        val result = AudioRepository.audioToText(
//            TencentAudioRequestData(
//                engServiceType = "16k_zh",
//                voiceFormat = "aac",
//                sourceType = 1
//            ).setFileData(audioFile)
//        ).response.result
//        msg = result
        msg = OpenaiRepository.transcriptions(audioFile).text
        val language: String = Locale.getDefault().toString()
        if (language == "zh_CN") {
            msg = ChineseConverter.convert(msg, ConversionType.T2S, getApplication())
        }
    }

    var billEditableState by mutableStateOf<BillEditableState?>(null)
    var billEditable: BillEditable? = null

    suspend fun toJson() {
        val chatResult = OpenaiRepository.chat(
            ChatBody(
                listOf(
//                    Message(
//                        content = "你好，我可以从自然语言生成Json数据，有什么需要帮忙的吗？",
//                        role = Message.SystemUser
//                    ),
                    Message(
                        content = getChatContent(msg)
                    )
                )
            )
        )
        val result = extractJsonFromString(chatResult.choices.first().message.content) ?: ""
        val json = moshi.adapter<BillEditable>().fromJson(result) ?: throw Exception("解析数据失败")
//        if (json.category.codePointCount(0,json.category.length)>20) json.category = json.category.substring()
        billEditable = json
        billEditableState = json.getEditableState()
    }

    private fun getChatContent(msg: String): String {
        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val now = LocalDateTime.now()
        val nowString = now.format(dateTimeFormatter)
        val week = now.dayOfWeek.name
        val categoryJson = moshi.adapter<Categories>().toJson(CategoryRepository.getCategories())
        return """
            帮我解析“${msg}”变成下面的json格式，其中 category 从这个json里面选择：$categoryJson, 没有的信息用空字符串表示，
            除了备注以外别的字段不能有换行符，
            可以根据name字段得出category，transaction_partner，现在的时间是 $nowString , 今天是星期 $week ，
            {
                // amount 类型为字符串
                "amount": "1000",
                "comment": "备注",
                "datetime": "yyyy-MM-dd HH:mm:ss",
                "category": "消费类别或者收入类别",
                "transaction_partner": "消费去向或者收入来源",
                "name": "交易名称"
                // type: 支出：out, 收入：in
                "type": "out"
            }
        """.trimIndent()
    }

    suspend fun addBill(bill: BillEditableState) {
        BillRepository.addBill(bill.toBill())
    }

    override fun onCleared() {
        super.onCleared()
        audioFile.delete()
    }

}