import com.eazywrite.app.util.toBase64
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.File

private fun encodeFileToBase64(file: File): String {
    val fileBytes = file.readBytes()
    return android.util.Base64.encode(fileBytes, android.util.Base64.DEFAULT)
        .toString(Charsets.UTF_8)
}

@JsonClass(generateAdapter = true)
data class TencentAudioRequestData(
    @Json(name = "EngSerViceType") val engServiceType: String,
    @Json(name = "VoiceFormat") val voiceFormat: String,
    @Json(name = "SourceType") val sourceType: Int,
    @Json(name = "DataLen") var dataLen: Long = 0,
    @Json(name = "Data") var data: String = ""
) {
    fun setFileData(file: File): TencentAudioRequestData {
        dataLen = file.length()
        data = file.readBytes().toBase64()
        return this
    }
}

data class TencentAudioResponse(
    @Json(name = "Response")
    val response: TencentAudioResponseData
)

@JsonClass(generateAdapter = true)
data class TencentAudioResponseData(
    @Json(name = "RequestId") val requestId: String,
    @Json(name = "Result") val result: String,
    @Json(name = "AudioDuration") val audioDuration: Long,
    @Json(name = "WordSize") val wordSize: Int,
    @Json(name = "WordList") val wordList: List<String>?
)