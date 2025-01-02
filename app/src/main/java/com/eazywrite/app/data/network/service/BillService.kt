package com.eazywrite.app.data.network.service

import com.eazywrite.app.data.model.*
import retrofit2.http.*

interface BillService {
    @GET("/bill")
    suspend fun getBills(@Query("offset") offset: Int = -1, @Query("limit") limit: Int = -1): GetBillResponse

    @POST("/bill")
    suspend fun addBill(@Body bill : Bill): PostAndPutBillResponse

    @POST("/bill/list")
    suspend fun addBillList(@Body bill : List<Bill>): PostAndPutBillResponse

    @PUT("/bill")
    suspend fun updateBill(@Body bill : Bill): PostAndPutBillResponse

    @DELETE("/bill")
    suspend fun deleteBill(@Query("id") id: Long): DeleteResponse
}