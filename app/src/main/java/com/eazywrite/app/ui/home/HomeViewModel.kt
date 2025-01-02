package com.eazywrite.app.ui.home

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.eazywrite.app.data.model.Bill
import com.eazywrite.app.data.model.Categories
import com.eazywrite.app.data.repository.BillRepository
import com.eazywrite.app.data.repository.CategoryRepository
import com.eazywrite.app.data.repository.UserRepository
import com.eazywrite.app.ui.bill.BillEditableState
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.time.LocalDate

class HomeViewModel() : ViewModel() {

    var billEditableStateMap = mutableStateMapOf<Bill, BillEditableState>()

    fun getPagingData(
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
        category: String? = null,
        type: String? = null,
    ): Flow<PagingData<Bill>> {
        return BillRepository.getPagingDate(startDate, endDate, category, type).cachedIn(viewModelScope)
    }

    fun getAllId(): Flow<List<Int>> {
        return BillRepository.getAllId()
    }

    fun getAllFlow(): Flow<List<Bill>> {
        return BillRepository.getAllFlow()
    }

    suspend fun getActiveUser() = UserRepository.getCurrentUser()

    fun getAllBillByDate(date: LocalDate): Flow<List<Bill>> {
        return BillRepository.getAllByDate(date)
    }

    fun getAllDateFlow(
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
        category: String? = null,
        limit: Int = 20
    ): Flow<List<LocalDate>> = BillRepository.getAllDateFlow(startDate, endDate, category, limit)

    fun getTotalAmountFlow(
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
        category: String? = null,
        type: String,
    ): Flow<BigDecimal> = BillRepository.getTotalAmountFlow(startDate, endDate, category, type)

    suspend fun updateBill(bill: Bill) = BillRepository.update(bill)

    suspend fun syncBills() = BillRepository.syncBills()

    suspend fun deleteBill(bill: Bill) = BillRepository.delete(bill)

    suspend fun getMaxDate() = BillRepository.getMaxDate()

    suspend fun getAllCategories(): Categories = CategoryRepository.getAllDBCategories()

}