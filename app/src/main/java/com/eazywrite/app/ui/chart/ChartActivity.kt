@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class
)

package com.eazywrite.app.ui.chart

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eazywrite.app.R
import com.eazywrite.app.common.toast
import com.eazywrite.app.ui.theme.EazyWriteTheme
import com.eazywrite.app.ui.wiget.MarkdowmTextView
import com.eazywrite.app.util.fillMaxSize
import com.eazywrite.app.util.setWindow
import com.eazywrite.app.util.toArgbHex
import com.eazywrite.app.util.toRgbHex
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAStyle
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate


class ChartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setWindow()

        setContent {
            EazyWriteTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
//                    ChartPage(paddingValues)
                }
            }
        }
    }
}

const val TAG = "ChartActivity.kt"

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ChartPage() {
    val vm: ChartViewModel = viewModel()

    var tab by rememberSaveable {
        mutableStateOf(0)
    }

    var showYearPicker by rememberSaveable {
        mutableStateOf(false)
    }

    var showYearMonthPicker by rememberSaveable {
        mutableStateOf(false)
    }

    var yearOfYearView by rememberSaveable {
        mutableStateOf(LocalDate.now().year)
    }

    var yearOfMonthView by rememberSaveable {
        mutableStateOf(LocalDate.now().year)
    }

    var monthOfMonthView by rememberSaveable {
        mutableStateOf(LocalDate.now().month.value)
    }


    if (showYearPicker) {
        com.eazywrite.app.ui.wiget.YearPicker(
            currentYear = yearOfYearView,
            onSelected = {
                showYearPicker = false
                yearOfYearView = it
                vm.refresh(yearOfYearView)
            },
            onDismissRequest = { showYearPicker = false })
    }

    if (showYearMonthPicker) {
        com.eazywrite.app.ui.wiget.YearMonthPicker(
            currentYear = yearOfMonthView,
            currentMonth = monthOfMonthView,
            onSelected = { year, month ->
                showYearMonthPicker = false
                yearOfMonthView = year
                monthOfMonthView = month
                vm.refresh(yearOfMonthView, monthOfMonthView)
            },
            onDismissRequest = { showYearMonthPicker = false })
    }

    fun refresh() {
        if (tab == 0) vm.refresh(yearOfYearView) else vm.refresh(
            yearOfMonthView,
            monthOfMonthView
        )
    }

    LaunchedEffect(key1 = Unit, block ={
        vm.getAllId().collect{
            refresh()
        }
    })

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(text = stringResource(id = R.string.chart))
            },
            actions = {
                var showReportDialog by rememberSaveable {
                    mutableStateOf(false)
                }
                var isLoading by rememberSaveable {
                    mutableStateOf(false)
                }
                var job = remember<Job?> { null }
                fun generateReports() {
                    if (vm.report != null) {
                        showReportDialog = true
                    } else {
                        toast("正在生成报告")
                        val year = if (tab == 0) yearOfYearView else yearOfMonthView
                        val month = if (tab == 0) null else monthOfMonthView
                        isLoading = true
                        job = vm.generateReports1(
                            year,
                            month,
                            onSuccess = {
                                showReportDialog = true
                            },
                            onFailure = {
                                toast("失败：${it.message}")
                            },
                            onComplete = {
                                isLoading = false
                            }
                        )
                    }
                }
                fun generateReports2(){
                    if (!isLoading) {
                        generateReports()
                    }else {
                        toast("已取消生成报告")
                        job?.cancel()
                    }
                }
                if (showReportDialog && vm.report != null) {
                    AlertDialog(onDismissRequest = { showReportDialog = false },
                        title = {},
                        text = {
                            Column(Modifier.verticalScroll(rememberScrollState())) {
                                SelectionContainer {
                                    MarkdowmTextView(text = vm.report ?: "")
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showReportDialog = false }) {
                                Text(text = "确定")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                vm.report = null
                                showReportDialog = false
                                generateReports2()
                            }) {
                                Text(text = "重新生成")
                            }
                        })
                }
                TextButton(onClick = {
                    generateReports2()
                }) {
                    Text(text = if (!isLoading) "生成报告" else "生成中（点击取消）", style = MaterialTheme.typography.titleMedium)
                }
                if (tab == 0) {
                    TextButton(onClick = {
                        showYearPicker = true
                    }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${yearOfYearView}年",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Icon(imageVector = Icons.Default.ExpandMore, contentDescription = null)
                        }
                    }
                } else {
                    TextButton(onClick = {
                        showYearMonthPicker = true
                    }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${yearOfMonthView}年${monthOfMonthView}月",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Icon(imageVector = Icons.Default.ExpandMore, contentDescription = null)
                        }
                    }
                }
            }
        )
    }) { pad ->

        val refreshing by rememberSaveable {
            mutableStateOf(false)
        }
        val scope = rememberCoroutineScope()
        val pullRefreshState = rememberPullRefreshState(refreshing = refreshing, onRefresh = {
            scope.launch {
                refresh()
            }
        })
        LaunchedEffect(key1 = Unit, block = {
            refresh()
        })
        Column(
            Modifier
                .padding(pad)
                .fillMaxSize()
        ) {

            TabRow(selectedTabIndex = tab) {
                val tabGroup = remember {
                    listOf("年视图", "月视图")
                }
                tabGroup.forEachIndexed { i, item ->
                    Tab(
                        selected = tab == i,
                        onClick = {
                            if (tab != i) {
                                tab = i
                                refresh()
                            }
                        },
                        text = { Text(text = item) },
                    )
                }
            }

            ChartContent(pullRefreshState, refreshing)

        }

    }
}

@Composable
private fun ChartContent(
    pullRefreshState: PullRefreshState,
    refreshing: Boolean
) {
    Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
        Column(
            modifier = Modifier
//                        .padding(it)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
            ) {
                LineChart1()
            }
            Spacer(modifier = Modifier.padding(32.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                PieChartOut()
            }
            Spacer(modifier = Modifier.padding(32.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                PieChartIn()
            }
            Spacer(modifier = Modifier.height(100.dp))

        }
        PullRefreshIndicator(
            refreshing,
            pullRefreshState,
            Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun LineChart1() {
    val primary = MaterialTheme.colorScheme.primary
    val onBackground = MaterialTheme.colorScheme.onBackground
    val background = MaterialTheme.colorScheme.background
    val width = with(LocalDensity.current) { 0.5.dp.toPx() }
    val vm: ChartViewModel = viewModel()

    fun aaChartModel() = AAChartModel(
        legendEnabled = true,
        yAxisTitle = "",
        dataLabelsEnabled = true,
        chartType = AAChartType.Line,
        title = "趋势分析",
        backgroundColor = Color.Transparent.toArgbHex(),
        series = vm.lineChartData.toTypedArray(),
        categories = vm.lineChartXData.toTypedArray(),
        titleStyle = AAStyle().color(onBackground.toRgbHex())
    )

    AndroidView(
        factory = { context ->
            AAChartView(context).fillMaxSize().apply {
                aa_drawChartWithChartModel(aaChartModel())
            }
        }, update = {
            it.apply {
                aa_refreshChartWithChartModel(aaChartModel())
            }
        }
    )
}


@Composable
private fun PieChartOut() {
    val primary = MaterialTheme.colorScheme.primary
    val onBackground = MaterialTheme.colorScheme.onBackground
    val background = MaterialTheme.colorScheme.background
    val width = with(LocalDensity.current) { 0.5.dp.toPx() }
    val vm: ChartViewModel = viewModel()

    fun aaChartModel() = AAChartModel(
        legendEnabled = false,
        yAxisTitle = "消费分析",
        dataLabelsEnabled = true,
        chartType = AAChartType.Pie,
        title = "消费分析",
        backgroundColor = Color.Transparent.toArgbHex(),
        series = arrayOf(
            vm.pieChartOutData,
        ).mapNotNull { it }.toTypedArray(),
        titleStyle = AAStyle().color(onBackground.toRgbHex())
    )

    AndroidView(
        factory = { context ->
            AAChartView(context).fillMaxSize().apply {
                aa_drawChartWithChartModel(aaChartModel())
            }
        }, update = {
            it.apply {
                aa_refreshChartWithChartModel(aaChartModel())
            }
        }
    )
}


@Composable
private fun PieChartIn() {
    val primary = MaterialTheme.colorScheme.primary
    val onBackground = MaterialTheme.colorScheme.onBackground
    val background = MaterialTheme.colorScheme.background
    val width = with(LocalDensity.current) { 0.5.dp.toPx() }
    val vm: ChartViewModel = viewModel()

    fun aaChartModel() = AAChartModel(
        legendEnabled = false,
        yAxisTitle = "收入分析",
        dataLabelsEnabled = true,
        chartType = AAChartType.Pie,
        title = "收入分析",
        backgroundColor = Color.Transparent.toArgbHex(),
        series = arrayOf(
            vm.pieChartInData,
        ).mapNotNull { it }.toTypedArray(),
        titleStyle = AAStyle().color(onBackground.toRgbHex())
    )

    AndroidView(
        factory = { context ->
            AAChartView(context).fillMaxSize().apply {
                aa_drawChartWithChartModel(aaChartModel())
            }
        }, update = {
            it.apply {
                aa_refreshChartWithChartModel(aaChartModel())
            }
        }
    )
}
