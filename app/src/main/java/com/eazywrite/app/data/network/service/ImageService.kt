package com.eazywrite.app.data.network.service

import com.eazywrite.app.data.model.*
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * @author wilinz
 * @date 2023/3/3 9:17
 */
interface ImageService {
    /**
     * 国内通用票据识别
     *
     * 功能描述
     * 支持对多种票据类型（多票据）票据切分、票据分类、票据识别，包括增值税普通发票、增值税普通发票（卷票）、增值税专用发票、
     * 增值税电子专用发票、增值税电子普通发票、货物运输业增值税专用发票、机动车销售统一发票、二手车销售统一发票、通用机打发票、
     * 通用定额发票、旅客运输普票、公路客运发票、船运客票、出租车发票、停车费发票、过路过桥费发票、教育费收据、行程单、火车票、
     * 增值税销货清单和其他可报销票据。
     * @return
     */
    @POST("/robot/v1.0/api/bills_crop")
    suspend fun billsRecognition(@Body body: RequestBody): BillsRecognitionResponse

    /**
     * 功能描述:
     * 图像切边增强
     * 裁切图像主体区域并增强
     *
     * @param enhance_mode        1 增亮
     * 2 增亮并锐化
     * 3 黑白
     * 4 灰度
     * 5 去阴影增强
     * 6 点阵图
     * -1 禁用增强
     * @param correct_direction 0 不校正图片方向，默认为0
     *                          1 校正图片方向
     * @return
     */
    @POST("/ai/service/v1/crop_enhance_image")
    suspend fun cropEnhanceImage(
        @Query("enhance_mode") enhanceMode: Int,
        @Query("correct_direction") correctDirection: Int,
        @Body body: RequestBody?
    ): CropEnhanceImageResponseKt

    /**
     * 功能描述
     * 图像切边矫正
     * <p>
     * 文档提取-->形变矫正-->边缘填充
     *
     * crop 是否切边（即文档提取）；0不切边；1切边
     * inpainting 是否边缘填充； 0不填充；1填充
     */
    @POST("/ai/service/v1/dewarp")
    suspend fun imageCorrection(
        @Query("crop") crop: Int,
        @Query("inpainting") edgePadding: Int,
        @Body body: RequestBody?
    ): ImageCorrectionResponse
}