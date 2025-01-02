package com.eazywrite.app.data.repository

import android.net.Uri
import com.eazywrite.app.data.model.*
import com.eazywrite.app.data.network.Network
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

object ImagesRepository {

    val images = MutableStateFlow<List<Uri>>(listOf())
    private val octetStreamType =  "application/octet-stream".toMediaType()

    suspend fun cropEnhanceImage(imageFile: File): CropEnhanceImageResponseKt {
        val body = imageFile.asRequestBody(octetStreamType)
        return Network.imagesService.cropEnhanceImage(2, 1, body)
    }

    suspend fun imageCorrection(imageFile: File): ImageCorrectionResponse {
        val body = imageFile.asRequestBody(octetStreamType)
        return Network.imagesService.imageCorrection(1, 1, body)
    }

    suspend fun billsRecognition(imageFile: File): BillsRecognitionResponse {
        val body = imageFile.asRequestBody(octetStreamType)
        return Network.imagesService.billsRecognition(body)
    }
}