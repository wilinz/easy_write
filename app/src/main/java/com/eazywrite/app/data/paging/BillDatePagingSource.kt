package com.eazywrite.app.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.eazywrite.app.data.model.Bill
import com.eazywrite.app.data.repository.BillRepository
import java.time.LocalDate

class BillDatePagingSource(
  val repository: BillRepository = BillRepository,
  val startDate: LocalDate? = null,
  val endDate: LocalDate? = null,
  val category: String? = null,
  val type: String? = null,
) : PagingSource<Int, Bill>() {
  override suspend fun load(
    params: LoadParams<Int>
  ): LoadResult<Int, Bill> {
    return try {
      repository.getAllPaging(startDate, endDate, category, type).load(params)
    } catch (e: Exception) {
      LoadResult.Error(e)
    }
  }

  override fun getRefreshKey(state: PagingState<Int, Bill>): Int? {
    // 尝试从 prevKey 或 nextKey 中找到最接近 anchorPosition 的页面的页面键，但您需要在此处处理可空性：
    //  * prevKey == null -> anchorPage is the first page.
    //  * nextKey == null -> anchorPage is the last page.
    //  * both prevKey and nextKey null -> anchorPage is the initial page, so
    //    just return null.
    return state.anchorPosition
  }

}