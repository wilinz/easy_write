@file:OptIn(ExperimentalStdlibApi::class)

package com.eazywrite.app.ui.image_editing

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import com.eazywrite.app.data.model.Bill
import com.eazywrite.app.data.model.BillEditable
import com.eazywrite.app.data.model.BillsCropResult
import com.eazywrite.app.data.model.Categories
import com.eazywrite.app.data.model.ChatBody
import com.eazywrite.app.data.model.ChatResponse
import com.eazywrite.app.data.model.Message
import com.eazywrite.app.data.model.StringList
import com.eazywrite.app.data.model.mapToOcrResults
import com.eazywrite.app.data.moshi
import com.eazywrite.app.data.repository.BillRepository
import com.eazywrite.app.data.repository.CategoryRepository
import com.eazywrite.app.data.repository.ImagesRepository
import com.eazywrite.app.data.repository.NotLoggedInException
import com.eazywrite.app.data.repository.OpenaiRepository
import com.eazywrite.app.ui.bill.BillEditableState
import com.eazywrite.app.ui.bill.getEditableState
import com.eazywrite.app.util.base64ToFile
import com.eazywrite.app.util.bitmapToFile
import com.eazywrite.app.util.decodeImageFromFile
import com.eazywrite.app.util.extractJsonFromString
import com.eazywrite.app.util.getImageRotationDegrees
import com.eazywrite.app.util.toGrayscale
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.squareup.moshi.adapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

data class FileWrapper(
    val file: File,
    val bill: Bill? = null,
    val billEditable: BillEditableState? = null,
    val recognitionResult: BillsCropResult? = null
)

class ImagePreviewViewModel(application: Application) : AndroidViewModel(application) {

    val imageFiles = mutableStateListOf<FileWrapper>()

    suspend fun objectDelete(context: Context, file: File): Bitmap = withContext(Dispatchers.Default) {
        // 创建对象检测器
        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
            .build()
        val objectDetector = ObjectDetection.getClient(options)

        val bitmap = decodeImageFromFile(file.path)?.toGrayscale() ?: throw Exception("打开文件失败")
        // 创建一个InputImage对象
        val image = InputImage.fromBitmap(bitmap, getImageRotationDegrees(file.path))

        suspendCoroutine { suspend ->
            objectDetector.process(image)
                .addOnSuccessListener { objects ->
                    // 根据检测结果，剪裁图片
                    if (objects.size > 0) {
                        val boundingBox = objects[0].boundingBox // 获取第一个检测到的对象的边界框
                        val croppedBitmap = Bitmap.createBitmap(
                            bitmap,
                            boundingBox.left,
                            boundingBox.top,
                            boundingBox.width(),
                            boundingBox.height()
                        ) // 剪裁图片
                        suspend.resume(croppedBitmap)
                    } else {
                        suspend.resume(bitmap)
                    }
                }
                .addOnFailureListener { e ->
                    suspend.resumeWithException(e)
                }
        }
        // 对图片进行对象检测

    }

    suspend fun cropEnhanceImage(imageFileIndex: Int): File {
        val file = imageFiles[imageFileIndex]

        val image = kotlin.runCatching {
            objectDelete(getApplication(),file.file)
//            val resp = ImagesRepository.cropEnhanceImage(file.file)
//            if (resp.result.imageList.isEmpty()) throw Exception("返回数据为空")
//            base64ToBitmap(resp.result.imageList.first().image)
        }.getOrNull() ?: kotlin.runCatching {
            objectDelete(getApplication(),file.file)
        }.getOrNull() ?: throw Exception("增强失败")

        val newFile = bitmapToFile(file.file, image)
        imageFiles[imageFileIndex] = file.copy(file = newFile)
        return imageFiles[imageFileIndex].file
    }

    suspend fun imageCorrection(imageFileIndex: Int): File {
        val file = imageFiles[imageFileIndex]
        val resp = ImagesRepository.imageCorrection(file.file)
        val newFile = base64ToFile(file.file, resp.result.image)
        imageFiles[imageFileIndex] = file.copy(file = newFile)
        return imageFiles[imageFileIndex].file
    }

    private suspend fun mlkitOcr(file: File): Text = withContext(Dispatchers.Default) {
        val recognizer = TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())
        suspendCoroutine { suspend ->
            recognizer.process(InputImage.fromFilePath(getApplication(), file.toUri()))
                .addOnSuccessListener { visionText ->
                    suspend.resume(visionText)
                }
                .addOnFailureListener { e ->
                    suspend.resumeWithException(e)
                }
        }
    }

    private suspend fun Text.getSortedText(): String = withContext(Dispatchers.Default) {
        val text = this@getSortedText.mapToOcrResults()
        text.sort()
        Log.d(TAG, "getSortedText: $text")
        text.toArray(1).map { it.text }.joinToString(separator = "")
    }

    suspend fun billsRecognition(imageFileIndex: Int) {
        fun fileWrapper() = imageFiles[imageFileIndex]

        suspend fun getOcrResult(useTextin: Boolean): ChatResponse {
            val data =
                if (useTextin) {
                    val textinResp1 =
                        kotlin.runCatching { ImagesRepository.billsRecognition(fileWrapper().file) }
                            .onFailure { it.printStackTrace() }.getOrNull()
                    if (textinResp1 != null) {
                        moshi.adapter<BillsCropResult>().toJson(textinResp1.result)
                    } else {
                        mlkitOcr(fileWrapper().file).getSortedText()
                    }
                } else {
                    mlkitOcr(fileWrapper().file).getSortedText()
                }

            Log.d(TAG, "billsRecognition: $data")

            return kotlin.runCatching {
                OpenaiRepository.chat(
                    ChatBody(
                        listOf(
                            Message(
                                content = getChatContent(data)
                            )
                        )
                    )
                )
            }.getOrElse {
                if (it is NotLoggedInException) {
                    throw Exception("请登录")
                } else {
                    throw Exception("服务器出错，请重试")
                }
            }

        }

        suspend fun getJson(useTextin: Boolean): BillEditable {
            val msg = getOcrResult(useTextin).choices.first().message.content
            val result = extractJsonFromString(msg) ?: ""
            return kotlin.runCatching { moshi.adapter<BillEditable>().fromJson(result) }.getOrNull()
                ?: throw Exception(msg)
        }

        var json = kotlin.runCatching {
            getJson(false)
        }.getOrNull() ?: kotlin.runCatching {
            getJson(false)
        }.getOrElse {
            if (it is NotLoggedInException) {
                throw Exception("请登录")
            } else {
                throw Exception("${it.message}")
            }
        }

        val amount = if (json.amount.startsWith('-')) json.amount.removePrefix("-") else json.amount
        json = json.copy(amount = amount, imagesComment = StringList().apply { add(fileWrapper().file.toUri().toString()) })
        imageFiles[imageFileIndex] = fileWrapper().copy(billEditable = json.getEditableState())
    }

    suspend fun addBill(imageFileIndex: Int) {
        val bill = imageFiles[imageFileIndex].billEditable?.toBill() ?: throw Exception("格式错误")
        BillRepository.addBill(bill)
    }

    private fun getChatContent(json: String): String {
        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val now = LocalDateTime.now()
        val nowString = now.format(dateTimeFormatter)
        val categoryJson = moshi.adapter<Categories>().toJson(CategoryRepository.getCategories())
        return """
            帮我根据给你的数据变成下面的标准的json格式，要求：根据name字段得出category、transaction_partner,
            其中 category 从这个json里面选择：$categoryJson,
            如果 name 太长，则精简 name ，name 长度尽可能的不超过15个字符，
            除了备注以外别的字段不能有换行符，
            所有字段中都不能包含个人信息，如身份证，手机号，
            comment 不做处理，使用空字符串，
            如果不能从原数据推断出日期,则使用今天的日期，现在的时间是 $nowString ，但是要求尽可能的从原数据推断出日期，
标准的json：
{
    // amount 类型为字符串
    "amount": "1000",
    "comment": "备注",
    "datetime": "yyyy-MM-dd HH:mm:ss",
    "category": "消费类别或者收入类别",
    "transaction_partner": "消费去向或者收入来源",
    "name": "交易名称",
    // type: 支出：out, 收入：in
    "type": "out"
}
给你的数据：
$json
        """.trimIndent()
    }

}