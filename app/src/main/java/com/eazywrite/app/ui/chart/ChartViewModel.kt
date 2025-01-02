@file:OptIn(ExperimentalStdlibApi::class)

package com.eazywrite.app.ui.chart

import android.app.Application
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.eazywrite.app.data.database.db
import com.eazywrite.app.data.model.*
import com.eazywrite.app.data.moshi1
import com.eazywrite.app.data.repository.BillRepository
import com.eazywrite.app.data.repository.OpenaiRepository
import com.eazywrite.app.util.secureDivide
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.squareup.moshi.adapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * @author wilinz
 * @date 2023/4/3 16:57
 */
class ChartViewModel(application: Application) : AndroidViewModel(application) {

    var lineChartData by mutableStateOf<List<AASeriesElement>>(listOf())

    var lineChartXData: SnapshotStateList<String> = mutableStateListOf()

    var pieChartOutData by mutableStateOf<AASeriesElement?>(null)

    var pieChartInData by mutableStateOf<AASeriesElement?>(null)

    var tab by mutableStateOf(false)

    fun refresh(year: Int, month: Int? = null) {
        viewModelScope.launch {
            launch { getLineChartData(year, month) }
            launch { getPieDate(year, month) }
        }
    }

    private suspend fun getLineChartData(year: Int, month: Int? = null) {
        val labelList =
            if (month == null) getMonthListByYear(year) else getDayListByMonth(year, month)
        val xData = labelList.map {
            it.first.format(
                if (month == null) DateTimeFormatter.ofPattern(
                    "yyyy-MM"
                ) else DateTimeFormatter.ofPattern(
                    "MM-dd"
                )
            )
        }
        lineChartXData.apply {
            clear()
            addAll(xData)
        }
        val outData = labelList.map { (start, end) ->
            db.billDao().getTotalAmount(
                start, end, type = Bill.TYPE_OUT
            ) ?: BigDecimal.ZERO
        }
        val inData = labelList.map { (start, end) ->
            db.billDao().getTotalAmount(
                start, end, type = Bill.TYPE_IN
            ) ?: BigDecimal.ZERO
        }
        lineChartData = listOf(
            AASeriesElement().apply {
                name("消费")
                this.data(outData.map { it.toFloat() }.toTypedArray())
            },
            AASeriesElement().apply {
                name("收入")
                this.data(inData.map { it.toFloat() }.toTypedArray())
            }
        )
    }

    private fun getMonthListByYear(year: Int): List<Pair<LocalDate, LocalDate>> {
        val list = mutableListOf<Pair<LocalDate, LocalDate>>()
        val date = LocalDate.of(year, 1, 1).atStartOfDay()
        for (i in 0 until 12) {
            val start = date.plusMonths(i.toLong())
            val end = start.plusMonths(1)
            list.add(Pair(start.toLocalDate(), end.toLocalDate()))
        }
        return list
    }

    private fun getDayListByMonth(year: Int, month: Int): List<Pair<LocalDate, LocalDate>> {
        val list = mutableListOf<Pair<LocalDate, LocalDate>>()

        val date = LocalDate.of(year, month, 1).atStartOfDay()
        for (i in 0 until date.month.maxLength()) {
            val start = date.plusDays(i.toLong())
            val end = start.plusDays(1)
            list.add(Pair(start.toLocalDate(), end.toLocalDate()))
        }
        return list
    }

    private fun getYearRange(year: Int): Pair<LocalDate, LocalDate> {
        val start = LocalDate.of(year, 1, 1).atStartOfDay()
        return Pair(start.toLocalDate(), start.plusYears(1).toLocalDate())
    }

    private fun getMonthRange(year: Int, month: Int): Pair<LocalDate, LocalDate> {
        val start = LocalDate.of(year, month, 1).atStartOfDay()
        return Pair(start.toLocalDate(), start.plusMonths(1).toLocalDate())
    }

    private suspend fun getAllCategory(
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
        type: String,
    ): List<String> = BillRepository.getAllCategory(startDate, endDate, type)

    var report: String? by mutableStateOf(null)

    fun generateReports1(
        year: Int,
        month: Int? = null,
        onSuccess: () -> Unit,
        onFailure: (e: Throwable) -> Unit,
        onComplete: () -> Unit
    ): Job {
        return viewModelScope.launch {
            kotlin.runCatching {
                report = generateReports(year, month)
            }.onSuccess {
                onSuccess()
            }.onFailure {
                it.printStackTrace()
                onFailure(it)
            }
            onComplete()
        }
    }

    suspend fun generateReports(year: Int, month: Int? = null): String = withContext(Dispatchers.Default) {
        val desc = if (month != null) "这是我${year}年${month}月的一份账单数据" else "这是我${year}年的一份账单数据"
        val data = BillingAnalyticsData(
            percentageData = getPercentageData(year, month),
            trendData = getTrendData(year, month)
        )
        val dataJson = moshi1.adapter<BillingAnalyticsData>().toJson(data)
        Log.d(TAG, "generateReports: $dataJson")
        val chatContent = "${desc}，帮我我生成一份形成用户行为分析报告，并给我消费指导建议，以markdown格式回答我（不要使用表格），数据如下：\n${dataJson}"
        val chatResult = OpenaiRepository.chat(
            ChatBody(
                listOf(
                    Message(
                        content = chatContent
                    )
                )
            )
        )
        return@withContext chatResult.choices.first().message.content
    }

    fun getAllId(): Flow<List<Int>> {
        return BillRepository.getAllId()
    }

    suspend fun getTrendData(year: Int, month: Int? = null): FinancialAnalysisData {
        val labelList =
            if (month == null) getMonthListByYear(year) else getDayListByMonth(year, month)
        val xData = labelList.map {
            it.first.format(
                if (month == null) DateTimeFormatter.ofPattern(
                    "yyyy-MM"
                ) else DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd"
                )
            )
        }
        val outData = labelList.map { (start, end) ->
            BillRepository.getTotalAmount(
                start, end, type = Bill.TYPE_OUT
            )
        }
        val inData = labelList.map { (start, end) ->
            BillRepository.getTotalAmount(
                start, end, type = Bill.TYPE_IN
            )
        }
        return FinancialAnalysisData(
            inTotal = inData.sumOf { it },
            outTotal = outData.sumOf { it },
            inData = inData.mapIndexed { index, amount -> TrendData(date = xData[index], amount) },
            outData = outData.mapIndexed { index, amount ->
                TrendData(
                    date = xData[index],
                    amount
                )
            },
        )
    }

    suspend fun getPercentageData(year: Int, month: Int? = null): PercentageData {
        val dateRange = if (month == null) getYearRange(year) else getMonthRange(year, month)
        val outCategories = getAllCategory(dateRange.first, dateRange.second, Bill.TYPE_OUT)
        val inCategories = getAllCategory(dateRange.first, dateRange.second, Bill.TYPE_IN)
        val outData = outCategories.map {
            BillRepository.getTotalAmount(dateRange.first, dateRange.second, it, Bill.TYPE_OUT)
        }
        val inData = inCategories.map {
            BillRepository.getTotalAmount(dateRange.first, dateRange.second, it, Bill.TYPE_IN)
        }

        val inData2 =
            mapOf(*inData.mapIndexed { index, amount -> Pair(inCategories[index], amount) }
                .toTypedArray())

        val outData2 =
            mapOf(*outData.mapIndexed { index, amount -> Pair(outCategories[index], amount) }
                .toTypedArray())

        val inTotal = inData.sumOf { it }
        val outTotal = outData.sumOf { it }



        return PercentageData(
            dateRange = DateRange(start = dateRange.first, end = dateRange.second),
            inTotal = inTotal,
            outTotal = outTotal,
            inData = inData2.map {
                AnalyseData(
                    category = it.key,
                    amount = it.value,
                    percentage = (it.value.secureDivide(inTotal, 4)).toDouble()
                )
            },
            outData = outData2.map {
                AnalyseData(
                    category = it.key,
                    amount = it.value,
                    percentage = (it.value.secureDivide(outTotal, 4)).toDouble()
                )
            }
        )
    }

    init {
        val outData1 = listOf(
            listOf(
                "暂无支出",
                0.00f
            )
        )
        pieChartOutData = AASeriesElement().apply {
            name("金额")
            data(outData1.toTypedArray())
        }
        val inData1 = listOf(
            listOf(
                "暂无收入",
                0.00f
            )
        )
        pieChartInData = AASeriesElement().apply {
            name("金额")
            data(inData1.toTypedArray())
        }
    }

    private suspend fun getPieDate(year: Int, month: Int? = null) {
        val dateRange = if (month == null) getYearRange(year) else getMonthRange(year, month)
        val outCategories = getAllCategory(dateRange.first, dateRange.second, Bill.TYPE_OUT)
        val inCategories = getAllCategory(dateRange.first, dateRange.second, Bill.TYPE_IN)
        val outData = outCategories.map {
            BillRepository.getTotalAmount(dateRange.first, dateRange.second, it, Bill.TYPE_OUT)
        }
        val inData = inCategories.map {
            BillRepository.getTotalAmount(dateRange.first, dateRange.second, it, Bill.TYPE_IN)
        }

        var outData1 = outData.mapIndexed { index, money ->
            listOf(
                outCategories[index],
                money.toFloat()
            )
        }

        if (outData1.isEmpty()) {
            outData1 = listOf(
                listOf(
                    "暂无支出",
                    0.00f
                )
            )
        }

        pieChartOutData = AASeriesElement().apply {
            name("金额")
            data(outData1.toTypedArray())
        }

        var inData1 =
            inData.mapIndexed { index, money -> listOf(inCategories[index], money.toFloat()) }

        if (inData1.isEmpty()) {
            inData1 = listOf(
                listOf(
                    "暂无收入",
                    0.00f
                )
            )
        }

        pieChartInData = AASeriesElement().apply {
            name("金额")
            data(inData1.toTypedArray())
        }
    }

}