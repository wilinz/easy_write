package com.eazywrite.app.util

import android.graphics.*
import android.util.Base64
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Bitmap.toGrayscale(): Bitmap {
    val bitmap = this
    val width = bitmap.width
    val height = bitmap.height
    val grayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(grayBitmap)
    val paint = Paint()
    val colorMatrix = ColorMatrix()
    colorMatrix.setSaturation(0f)
    val filter = ColorMatrixColorFilter(colorMatrix)
    paint.colorFilter = filter
    canvas.drawBitmap(bitmap, 0f, 0f, paint)
    return grayBitmap
}

fun base64ToBitmap(base64String: String): Bitmap? {
    try {
        val bytes = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

suspend fun getImageRotationDegrees(imagePath: String): Int = withContext(Dispatchers.IO) {
    val exif = ExifInterface(imagePath)
    val orientation = exif.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
    )
    return@withContext when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90
        ExifInterface.ORIENTATION_ROTATE_180 -> 180
        ExifInterface.ORIENTATION_ROTATE_270 -> 270
        else -> 0
    }
}

suspend fun decodeImageFromFile(path: String): Bitmap? = withContext(Dispatchers.Default) {
    // 读取图像元数据
    val exif = ExifInterface(path)
    val orientation = exif.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
    )

    // 解码图像文件
    val options = BitmapFactory.Options().apply {
        inPreferredConfig = Bitmap.Config.ARGB_8888
        inSampleSize = 1
    }
    val bitmap = BitmapFactory.decodeFile(path, options)

    // 旋转图像以匹配方向
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270)
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flipBitmap(bitmap, isHorizontal = true)
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> flipBitmap(bitmap, isHorizontal = false)
        else -> bitmap
    }
}

suspend fun rotateBitmap(source: Bitmap, degrees: Int): Bitmap = withContext(Dispatchers.Default) {
    val matrix = Matrix().apply {
        postRotate(degrees.toFloat())
    }
    Bitmap.createBitmap(
        source, 0, 0, source.width, source.height, matrix, true
    )
}

suspend fun flipBitmap(source: Bitmap, isHorizontal: Boolean): Bitmap =
    withContext(Dispatchers.Default) {
        val matrix = Matrix().apply {
            if (isHorizontal) {
                postScale(-1f, 1f)
            } else {
                postScale(1f, -1f)
            }
        }
        Bitmap.createBitmap(
            source, 0, 0, source.width, source.height, matrix, true
        )
    }
