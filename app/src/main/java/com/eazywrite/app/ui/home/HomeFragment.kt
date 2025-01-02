@file:OptIn(
    ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class
)

package com.eazywrite.app.ui.home

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.eazywrite.app.R
import com.eazywrite.app.common.toast
import com.eazywrite.app.data.model.Bill
import com.eazywrite.app.data.model.Categories
import com.eazywrite.app.data.repository.NotLoggedInException
import com.eazywrite.app.ui.audiorecord.AudioRecordActivity
import com.eazywrite.app.ui.bill.AddBillContentActivity
import com.eazywrite.app.ui.bill.BillEditAction
import com.eazywrite.app.ui.bill.BillEditDialog
import com.eazywrite.app.ui.bill.CategoryPickerDialog
import com.eazywrite.app.ui.bill.getEditableState
import com.eazywrite.app.ui.image_editing.CameraXActivity
import com.eazywrite.app.ui.importbill.AlipayImportActivity
import com.eazywrite.app.ui.importbill.WeChatImportActivity
import com.eazywrite.app.ui.theme.EazyWriteTheme
import com.eazywrite.app.ui.wiget.CircularProgressDialog
import com.eazywrite.app.util.reverse
import com.eazywrite.app.util.startActivity
import com.leinardi.android.speeddial.compose.FabWithLabel
import com.leinardi.android.speeddial.compose.SpeedDial
import com.leinardi.android.speeddial.compose.SpeedDialOverlay
import com.leinardi.android.speeddial.compose.SpeedDialState
import com.marosseleng.compose.material3.datetimepickers.date.ui.dialog.DatePickerDialog
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class HomeFragment @JvmOverloads constructor(
    private val paddingValues: PaddingValues = PaddingValues(
        0.dp
    )
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(this.requireContext()).apply {
            setContent {
                EazyWriteTheme {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        HomePage()
                    }
                }
            }
        }
    }


}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun HomePage() {

    val vm: HomeViewModel = viewModel()
    var startDate: LocalDate? by remember {
        mutableStateOf(null)
    }
    var endDate by remember {
        val date = LocalDate.now()
        mutableStateOf(date)
    }

    LaunchedEffect(key1 = Unit, block = {
        vm.getMaxDate()?.let {
            if (it.isAfter(endDate)) {
                endDate = it
            }
        }
    })

    var category by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    var type by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val context = LocalContext.current
    var speedDialState by rememberSaveable { mutableStateOf(SpeedDialState.Collapsed) }
    var overlayVisible: Boolean by rememberSaveable { mutableStateOf(speedDialState.isExpanded()) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar(
                currentType = type,
                currentCategory = category,
                category = category,
                onCategoryChanged = { type0, category0 ->
                    type = type0
                    category = category0
                })
        },
        floatingActionButton = {
            Fab(
                vm = vm,
                speedDialState = speedDialState,
                onSpeedDialStateChange = {
                    speedDialState = it
                },
                onOverlayVisible = {
                    overlayVisible = it
                })
        },
        containerColor = Color.Transparent,
    ) { paddingValues1 ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column {
                Box {
                    Image(
                        painter = painterResource(id = R.drawable.bill_background),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .height(152.dp)
                            .fillMaxWidth()
                    )
                    Box(modifier = Modifier.padding(top = paddingValues1.calculateTopPadding())) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val totalAmountOut by remember(startDate, endDate, category, type) {
                                vm.getTotalAmountFlow(
                                    startDate,
                                    endDate,
                                    when (type) {
                                        Bill.TYPE_OUT -> category
                                        null -> null
                                        else -> UUID.randomUUID()
                                            .toString()
                                    },
                                    Bill.TYPE_OUT
                                )
                            }.collectAsState(initial = 0.toBigDecimal())


                            val totalAmountOutText = rememberSaveable(totalAmountOut) {
                                String.format("￥%.2f", totalAmountOut)
                            }

                            val totalAmountIn by remember(startDate, endDate, category, type) {
                                vm.getTotalAmountFlow(
                                    startDate,
                                    endDate,
                                    when (type) {
                                        Bill.TYPE_IN -> category
                                        null -> null
                                        else -> UUID.randomUUID()
                                            .toString()
                                    },
                                    Bill.TYPE_IN
                                )
                            }.collectAsState(initial = 0.toBigDecimal())

                            val totalAmountInText = rememberSaveable(totalAmountIn) {
                                String.format("￥%.2f", totalAmountIn)
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "总支出", color = MaterialTheme.colorScheme.onPrimary)
                                Text(
                                    text = totalAmountOutText,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            Text(text = "|", color = MaterialTheme.colorScheme.onPrimary)
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "总收入", color = MaterialTheme.colorScheme.onPrimary)
                                Text(
                                    text = totalAmountInText,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }

                Box(
                    Modifier
                        .padding(top = 4.dp)
                        .fillMaxWidth(), Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        DateTimeSelect(
                            date = startDate,
                            defaultDisplay = "开始日期",
                            onDateChange = { startDate = it },
                            modifier = Modifier
                        )
                        Text(
                            text = " -> ",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        DateTimeSelect(
                            date = endDate,
                            defaultDisplay = "结束日期",
                            onDateChange = { endDate = it },
                            modifier = Modifier
                        )
                    }
                }

                val dateList = remember(startDate, endDate, category, type) {
                    vm.getPagingData(
                        startDate = startDate,
                        endDate = endDate,
                        category = category,
                        type = type
                    )
                }.collectAsLazyPagingItems()


                LaunchedEffect(key1 = Unit, block = {
                    vm.getAllId().collect {
                        dateList.refresh()
                    }
                })

                val scope = rememberCoroutineScope()
                var refreshing by rememberSaveable {
                    mutableStateOf(false)
                }
                val pullState = rememberPullRefreshState(refreshing = refreshing, onRefresh = {
                    scope.launch {
                        dateList.refresh()
                    }
                })

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    BillCard(
                        dateList = dateList,
                        pullState,
                        refreshing,
                    )
                }
            }
        }

        SpeedDialOverlay(
            visible = overlayVisible,
            onClick = {
                overlayVisible = false
                speedDialState = speedDialState.toggle()
            },
        )
    }

}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TopBar(
    currentType: String? = null,
    currentCategory: String? = null,
    category: String?,
    onCategoryChanged: (type: String?, category: String?) -> Unit,
) {
    var isLoading by rememberSaveable {
        mutableStateOf(false)
    }
    var job = remember<Job?> { null }
    if (isLoading) {
        CircularProgressDialog(
            onDismissRequest = {
                isLoading = false
                job?.cancel()
            }
        )
    }
    val vm: HomeViewModel = viewModel()
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        title = {
            Text(text = "账单", color = MaterialTheme.colorScheme.onPrimary)
        },
        actions = {
            var isShowDialog by rememberSaveable {
                mutableStateOf(false)
            }
            var categories by remember {
                mutableStateOf<Categories?>(null)
            }
            LaunchedEffect(key1 = isShowDialog, block = {
                if (isShowDialog) {
                    categories = vm.getAllCategories()
                }
            })
            if (isShowDialog && categories != null) {
                CategoryPickerDialog(
                    currentType = currentType,
                    currentCategory = currentCategory,
                    categories = categories!!,
                    onDismissRequest = { isShowDialog = false },
                    onSelected = { type, category ->
                        isShowDialog = false
                        onCategoryChanged(type, category)
                    }
                )
            }
            TextButton(onClick = {
                isShowDialog = true
            }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = category ?: "全部类别",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            IconButton(onClick = {
                job = vm.viewModelScope.launch {
                    isLoading = true
                    kotlin.runCatching {
                        vm.syncBills()
                    }.onSuccess {
                        toast(text = "同步成功")
                    }.onFailure {
                        it.printStackTrace()
                        toast(text = "同步失败：${it.message}")
                    }
                    isLoading = false
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = "同步",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    )
}

private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

@Composable
private fun DateTimeSelect(
    date: LocalDate?,
    defaultDisplay: String,
    onDateChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isShowDatePicker by rememberSaveable {
        mutableStateOf(false)
    }
    if (isShowDatePicker) {
        DatePickerDialog(
            onDismissRequest = { isShowDatePicker = false },
            onDateChange = {
                isShowDatePicker = false
                onDateChange(it)
            },
            title = { Text(text = "请选择日期") },
            initialDate = date,
        )
    }
    TextButton(
        onClick = {
            isShowDatePicker = true
        },
//        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onPrimary),
        modifier = modifier
    ) {
        val dateText = remember(date) {
            date?.format(dateFormatter) ?: defaultDisplay
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = dateText, fontSize = 18.sp, fontWeight = FontWeight.Normal)
            Icon(imageVector = Icons.Default.ExpandMore, contentDescription = null)
        }
    }
}

private suspend fun checkIsLoggedIn(
    viewModel: HomeViewModel
) {
    val ok = viewModel.getActiveUser() != null
    if (!ok) {
        toast(text = "您未登录，所有功能仅供预览")
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
private fun Fab(
    vm: HomeViewModel,
    speedDialState: SpeedDialState,
    onSpeedDialStateChange: (SpeedDialState) -> Unit,
    onOverlayVisible: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    SpeedDial(
        state = speedDialState,
        onFabClick = { expanded ->
            onOverlayVisible(!expanded)
            onSpeedDialStateChange(if (expanded) SpeedDialState.Collapsed else SpeedDialState.Expanded)
        },
        fabClosedContent = {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
        },
        fabOpenedContent = {
            Icon(imageVector = Icons.Default.Close, contentDescription = null)
        }
    ) {
        item {
            FabWithLabel(
                onClick = {
                    scope.launch {
                        checkIsLoggedIn(vm)
                        context.startActivity<AlipayImportActivity>()
                    }
                },
                labelContent = { Text(text = "支付宝导入") },
                labelBorder = AssistChipDefaults.assistChipBorder(
                    borderWidth = 0.dp,
                    borderColor = Color.Transparent
                )
            ) {
                Icon(painterResource(id = R.drawable.alipay), null)
            }
        }
        item {
            FabWithLabel(
                onClick = {
                    scope.launch {
                        checkIsLoggedIn(vm)
                        context.startActivity<WeChatImportActivity>()
                    }
                },
                labelContent = { Text(text = "微信导入") },
                labelBorder = AssistChipDefaults.assistChipBorder(
                    borderWidth = 0.dp,
                    borderColor = Color.Transparent
                )
            ) {
                Icon(painterResource(id = R.drawable.wechat), null)
            }
        }

        item {
            FabWithLabel(
                onClick = {
                    scope.launch {
                        checkIsLoggedIn(vm)
                        context.startActivity<AddBillContentActivity>()
                    }
                },
                labelContent = { Text(text = "手动添加") },
                labelBorder = AssistChipDefaults.assistChipBorder(
                    borderWidth = 0.dp,
                    borderColor = Color.Transparent
                )
            ) {
                Icon(painterResource(id = R.drawable.edit), null)
            }
        }
        item {
            FabWithLabel(
                onClick = {
                    scope.launch {
                        checkIsLoggedIn(vm)
                        context.startActivity<CameraXActivity>()
                    }
                },
                labelContent = { Text(text = "图片识别") },
                labelBorder = AssistChipDefaults.assistChipBorder(
                    borderWidth = 0.dp,
                    borderColor = Color.Transparent
                )
            ) {
                Icon(Icons.Outlined.PhotoCamera, null)
            }
        }
        item {
            FabWithLabel(
                onClick = {
                    scope.launch {
                        checkIsLoggedIn(vm)
                        context.startActivity<AudioRecordActivity>()
                    }
                },
                labelContent = { Text(text = "智能添加") },
                labelBorder = AssistChipDefaults.assistChipBorder(
                    borderWidth = 0.dp,
                    borderColor = Color.Transparent
                )
            ) {
                Icon(painterResource(id = R.drawable.ai), null)
            }
        }
    }
}

@Composable
private fun BillCard(
    dateList: LazyPagingItems<Bill>,
    pullState: PullRefreshState,
    refreshing: Boolean
) {
    val vm: HomeViewModel = viewModel()
    val state = rememberLazyListState()

    Box(
        Modifier
            .fillMaxSize()
            .pullRefresh(pullState)
    ) {

        AnimatedVisibility(
            visible = dateList.itemCount != 0,
            enter = fadeIn(
                animationSpec = spring(stiffness = Spring.StiffnessVeryLow)
            ),
            exit = fadeOut(animationSpec = spring(stiffness = Spring.StiffnessVeryLow))
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = state,
                contentPadding = PaddingValues(16.dp, 4.dp, 16.dp, 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                content = {
                    items(dateList) { item ->
                        if (item == null) return@items
                        BillItem(
                            bill = item,
                            modifier = Modifier.animateItemPlacement(
                                spring(
                                    stiffness = Spring.StiffnessLow,
                                    visibilityThreshold = IntOffset.VisibilityThreshold
                                )
                            )
                        )
                    }
                })
        }


        AnimatedVisibility(
            visible = dateList.itemCount == 0 && !refreshing,
            enter = fadeIn(
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
            ),
            exit = fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMediumLow))
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {

            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(id = R.drawable.empty),
                        contentDescription = "暂无数据, 快去添加吧！",
                        modifier = Modifier.size(84.dp)
                    )
                    Spacer(modifier = Modifier.padding(16.dp))
                    Text(
                        text = "暂无数据, 快去添加吧！",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
        PullRefreshIndicator(refreshing, pullState, Modifier.align(Alignment.TopCenter))
    }


}

private val dateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd")

@Composable
private fun BillItem(
    bill: Bill,
    modifier: Modifier
) {

    var showDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var showDeleteDialog by rememberSaveable {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val vm: HomeViewModel = viewModel()
    LaunchedEffect(key1 = showDialog, block = {
        if (showDialog) {
            vm.billEditableStateMap[bill] = bill.getEditableState()
        }
    })

    var isLoading by rememberSaveable {
        mutableStateOf(false)
    }
    var job = remember<Job?> { null }
    if (isLoading) {
        CircularProgressDialog(
            onDismissRequest = {
                isLoading = false
                job?.cancel()
            }
        )
    }

    if (showDialog && vm.billEditableStateMap[bill] != null) {

        BillEditDialog(billEditableState = vm.billEditableStateMap[bill]!!,
            BillEditAction.EDIT,
            onDismissRequest = { showDialog = false },
            onConfirmText = "修改",
            onConfirm = {
                job = vm.viewModelScope.launch {
                    isLoading = true
                    runCatching {
                        val row = vm.updateBill(vm.billEditableStateMap[bill]!!.toBill())
                        if (row == 0) throw SQLiteException("更新失败")
                    }.onSuccess {
                        toast("修改成功")
                        showDialog = false
                    }.onFailure { e ->
                        if (e is NotLoggedInException) {
                            toast("请先登录")
                        } else {
                            toast("修改失败: " + e.message)
                        }
                        e.printStackTrace()
                    }
                    isLoading = false
                }
            },
            moreButton = {
                TextButton(onClick = {
                    showDeleteDialog = true
                }) {
                    Text(text = "删除")
                }
            }
        )
    }


    if (showDeleteDialog) {
        AlertDialog(onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = "确定删除") },
            text = { Text(text = "删除账单") },
            confirmButton = {
                TextButton(onClick = {
                    job = vm.viewModelScope.launch {
                        isLoading = true
                        runCatching {
                            vm.deleteBill(bill)
                        }.onSuccess {
                            toast("删除成功")
                            showDialog = false
                        }.onFailure { e ->
                            if (e is NotLoggedInException) {
                                toast("请先登录")
                            } else {
                                toast("删除失败: " + e.message)
                            }
                            e.printStackTrace()
                        }
                        isLoading = false
                        showDialog = false
                        showDeleteDialog = false
                    }
                }) {
                    Text(text = "确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(text = "取消")
                }
            }
        )
    }
    ElevatedCard() {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                    showDialog = true
                }

                .padding(16.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(text = bill.name, overflow = TextOverflow.Ellipsis, maxLines = 1)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row {
                        Text(
                            text = bill.category,
                            style = MaterialTheme.typography.bodySmall
                        )
                        if (bill.transactionPartner.isNotBlank()) {
                            Text(
                                text = " | ",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = bill.transactionPartner,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    if (bill.comment.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "备注：${bill.comment}",
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                }

                Spacer(modifier = Modifier.width(8.dp))

                CompositionLocalProvider(LocalLayoutDirection provides LocalLayoutDirection.current.reverse()) {
                    Column(Modifier.fillMaxHeight()) {
                        CompositionLocalProvider(LocalLayoutDirection provides LocalLayoutDirection.current.reverse()) {
                            var prefix = if (bill.type == Bill.TYPE_OUT) "-" else "+"
                            if (bill.amount == 0.toBigDecimal()) prefix = ""
                            Text(
                                text = prefix + String.format(
                                    "%.2f",
                                    bill.amount
                                ),
                                color = if (bill.type == Bill.TYPE_OUT) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            val datetimeText = remember(bill) {
                                val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                                bill.datetime.format(dateFormat)
                            }
                            Text(
                                text = datetimeText,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }


            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
    EazyWriteTheme {
//        HomePage()
    }
}