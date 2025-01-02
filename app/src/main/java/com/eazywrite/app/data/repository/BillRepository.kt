package com.eazywrite.app.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.eazywrite.app.data.database.db
import com.eazywrite.app.data.model.Bill
import com.eazywrite.app.data.network.Network
import com.eazywrite.app.data.paging.BillDatePagingSource
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

object BillRepository {
    suspend fun getBills(): List<Bill> {
//        val user = UserRepository.getCurrentUser() ?: throw NotLoggedInException()
        return db.billDao().getAll()
    }

    suspend fun syncBills() {
        val user = UserRepository.getCurrentUser() ?: throw NotLoggedInException()
        val resp = Network.billService.getBills()
        resp.data?.let {
            db.billDao().deleteOld(user.username)
            db.billDao().insertAll(it)
        }
    }

    suspend fun addBill(bill: Bill) {
        if (bill.amount < 0.toBigDecimal()) throw Exception("金额不能为负数")
        val user = UserRepository.getCurrentUser() ?: throw NotLoggedInException()
        bill.username = user.username

        val responseData = kotlin.runCatching {
            Network.billService.addBill(bill)
        }.onFailure { it.printStackTrace() }
            .getOrNull()?.data?.getOrNull(0) ?: throw Exception("连接服务器失败")

        bill.cloudId = responseData.id
        bill.thirdPartyId = responseData.thirdPartyID
        bill.imagesComment = responseData.imagesComment
        bill.isSynced = true

        db.billDao().insert(bill)
    }

    suspend fun addBillList(bills: List<Bill>) {

        if (bills.any { it.amount < 0.toBigDecimal() }) throw Exception("金额不能为负数")

        val user = UserRepository.getCurrentUser() ?: throw NotLoggedInException()
        for (bill in bills) {
            bill.username = user.username
        }

        val cloudIds = kotlin.runCatching {
            Network.billService.addBillList(bills)
        }.onFailure { it.printStackTrace() }
            .getOrNull()?.data ?: throw Exception("连接服务器失败")

        if (cloudIds.isNotEmpty()) {
            for (i in bills.indices) {
                bills[i].cloudId = cloudIds[i].id
                bills[i].thirdPartyId = cloudIds[i].thirdPartyID
                bills[i].imagesComment = cloudIds[i].imagesComment
                bills[i].isSynced = true
            }
        } else {
            for (bill in bills) {
                bill.isSynced = false
            }
        }

        db.billDao().insertAll(bills)
    }

    suspend fun getBillsPagingSource(): PagingSource<Int, Bill> {
//        val user = UserRepository.getCurrentUser() ?: throw NotLoggedInException()
//        return db.billDao().getAll()
        throw Exception()
    }

    fun getPagingDate(
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
        category: String? = null,
        type: String? = null,
    ): Flow<PagingData<Bill>> {
        return Pager(
            config = PagingConfig(100),
            pagingSourceFactory = {
                BillDatePagingSource(
                    startDate = startDate,
                    endDate = endDate,
                    category = category,
                    type = type
                )
            }
        ).flow
    }

    fun getAllId(): Flow<List<Int>> {
        return db.billDao().getAllId()
    }

    fun getAllByDate(date: LocalDate): Flow<List<Bill>> {
        return db.billDao().getAllByDate(date)
    }

    @OptIn(FlowPreview::class)
    fun getAllFlow(): Flow<List<Bill>> {
        return db.billDao().getAllFlow()
    }

    fun getAllPaging(
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
        category: String? = null,
        type: String? = null,
    ): PagingSource<Int, Bill> {
        return db.billDao().getAllPaging(startDate, endDate, category, type)
    }

    fun getAllDate() = db.billDao().getAllDate()

    suspend fun update(bill: Bill): Int {
        if (bill.amount < 0.toBigDecimal()) throw Exception("金额不能为负数")
        val responseData = kotlin.runCatching {
            Network.billService.updateBill(bill)
        }.onFailure { it.printStackTrace() }.getOrNull()?.data?.getOrNull(0)
            ?: throw Exception("连接服务器失败")

        bill.cloudId = responseData.id
        bill.thirdPartyId = responseData.thirdPartyID
        bill.imagesComment = responseData.imagesComment

        return db.billDao().update(bill)
    }

    suspend fun delete(bill: Bill) {
        val resp = kotlin.runCatching {
            Network.billService.deleteBill(bill.cloudId)
        }.onFailure { it.printStackTrace() }.getOrNull() ?: throw Exception("连接服务器失败")
        if (resp.code == 200) {
            db.billDao().delete(bill)
        } else {
            bill.deletedAt = LocalDateTime.now()
            bill.isSynced = false
            db.billDao().update(bill)
        }
    }

    fun getAllDateFlow(
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
        category: String? = null,
        limit: Int = 20
    ): Flow<List<LocalDate>> = db.billDao().getAllDateFlow(startDate, endDate, category, limit)

    fun getTotalAmountFlow(
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
        category: String? = null,
        type: String,
    ): Flow<BigDecimal> = db.billDao().getTotalAmountFlow(startDate, endDate, category, type)
        .map { it ?: BigDecimal.ZERO }

    suspend fun getTotalAmount(
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
        category: String? = null,
        type: String,
    ): BigDecimal =
        db.billDao().getTotalAmount(startDate, endDate, category, type) ?: BigDecimal.ZERO

    suspend fun getMaxDate() = db.billDao().getMaxDate()

    suspend fun getAllCategory(
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
        type: String
    ): List<String> = db.billDao().getAllCategory(startDate, endDate, type)

    suspend fun getRecent(limit: Int): List<Bill> = db.billDao().getRecent(limit)

    fun getRecentFlow(limit: Int): Flow<List<Bill>> = db.billDao().getRecentFlow(limit)

    fun countBillFlow(
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
        category: String? = null,
        type: String? = null,
    ): Flow<Int> = db.billDao().countBillFlow(startDate, endDate, category, type)
}

const val TAG = "BillRepository.kt"