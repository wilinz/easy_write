@file:OptIn(ExperimentalMaterial3Api::class)

package com.eazywrite.app.ui.image_editing

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.eazywrite.app.common.toast
import com.eazywrite.app.data.repository.NotLoggedInException
import com.eazywrite.app.ui.bill.BillEditAction
import com.eazywrite.app.ui.bill.BillEditDialog
import com.eazywrite.app.ui.theme.EazyWriteTheme
import com.eazywrite.app.ui.wiget.CircularProgressDialog
import com.eazywrite.app.util.getSerializableArrayListExtraCompat
import com.eazywrite.app.util.setWindow
import com.squareup.moshi.JsonDataException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

class ImagePreviewActivity : AppCompatActivity() {

    companion object {

        const val KEY_IMAGES_URI = "KEY_IMAGES_URI"
        fun start(context: Context, files: List<File>) {
            context.startActivity(Intent(context, ImagePreviewActivity::class.java).apply {
                putExtra(KEY_IMAGES_URI, ArrayList(files))
            })
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @SuppressLint("ClickableViewAccessibility", "UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindow(isDarkStatusBarIcon = true)

        val files = intent?.getSerializableArrayListExtraCompat<File>(KEY_IMAGES_URI) ?: return

        setContent {
            EazyWriteTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val vm: ImagePreviewViewModel = viewModel()
                    val pageState = rememberPagerState(pageCount = { vm.imageFiles.size })
                    val context = LocalContext.current
                    var painter: AsyncImagePainter? by remember {
                        mutableStateOf(null)
                    }

                    LaunchedEffect(key1 = Unit) {
                        vm.imageFiles.addAll(files.map { FileWrapper(it) })
                    }

                    var showDialog by rememberSaveable {
                        mutableStateOf(false)
                    }

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

                    if (showDialog) {
                        vm.imageFiles[pageState.currentPage].billEditable?.let { bill ->
                            BillEditDialog(billEditableState = bill,
                                onDismissRequest = { showDialog = false },
                                billEditAction = BillEditAction.ADD,
                                onConfirm = {
                                    job = vm.viewModelScope.launch {
                                        isLoading = true
                                        kotlin.runCatching {
                                            vm.addBill(pageState.currentPage)
                                        }.onSuccess {
                                            toast("添加成功")
                                            showDialog = false
                                        }.onFailure { e ->
                                            if (e is NotLoggedInException) {
                                                toast("请先登录")
                                            } else {
                                                toast("添加失败: " + e.message)
                                            }
                                            e.printStackTrace()
                                        }
                                        isLoading = false
                                    }
                                }
                            )
                        }
                    }

                    var isShowControlUi by rememberSaveable {
                        mutableStateOf(true)
                    }

                    LaunchedEffect(key1 = isShowControlUi, block = {
                        setWindow(isDarkStatusBarIcon = isShowControlUi)
                    })

                    Scaffold(
                        containerColor = Color.Black,
                        modifier = Modifier.pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                isShowControlUi = !isShowControlUi
                            })
                        },
                        topBar = {
                            AnimatedVisibility(
                                visible = isShowControlUi,
                                enter = slideInVertically(
                                    initialOffsetY = { -it },
                                    animationSpec = tween(durationMillis = 250)
                                ),
                                exit = slideOutVertically(
                                    targetOffsetY = { -it },
                                    animationSpec = tween(durationMillis = 250)
                                )

                            ) {
                                TopAppBar(
                                    title = {
                                        Text(text = "第${pageState.currentPage + 1}/${vm.imageFiles.size}张")
                                    },
                                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                                    actions = {
                                        val scope = rememberCoroutineScope()
                                        IconButton(onClick = {
                                            if (pageState.canScrollBackward) {
                                                scope.launch {
                                                    pageState.animateScrollToPage(pageState.currentPage - 1)
                                                }
                                            }
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.NavigateBefore,
                                                contentDescription = null,
                                            )
                                        }
                                        IconButton(onClick = {
                                            if (pageState.canScrollForward) {
                                                scope.launch {
                                                    pageState.animateScrollToPage(pageState.currentPage + 1)
                                                }
                                            }
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.NavigateNext,
                                                contentDescription = null,
                                            )
                                        }
                                    }
                                )
                            }
                        },
                        bottomBar = {
                            AnimatedVisibility(
                                visible = isShowControlUi,
                                enter = slideInVertically(
                                    initialOffsetY = { it },
                                    animationSpec = tween(durationMillis = 250)
                                ),
                                exit = slideOutVertically(
                                    targetOffsetY = { it },
                                    animationSpec = tween(durationMillis = 250)
                                )
                            ) {
                                BottomAppBar(
                                    containerColor = MaterialTheme.colorScheme.background
                                ) {
                                    Spacer(modifier = Modifier.weight(1f))
                                    TextButton(
                                        onClick = {
                                            job = vm.viewModelScope.launch {
                                                isLoading = true
                                                kotlin.runCatching {
                                                    vm.cropEnhanceImage(pageState.currentPage)
                                                }.onSuccess {
                                                    painter?.onForgotten()
                                                    painter?.onRemembered()
                                                    toast("增强成功")
                                                }.onFailure {
                                                    it.printStackTrace()
                                                    if (it is JsonDataException) {
                                                        toast("增强失败，服务器出错了")
                                                    } else {
                                                        toast("增强失败: ${it.message}")
                                                    }
                                                }
                                                isLoading = false
                                            }
                                        },
                                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onBackground),
                                    ) {
                                        Text(text = "图片增强")
                                    }
                                    TextButton(
                                        onClick = {
                                            job = vm.viewModelScope.launch {
                                                isLoading = true
                                                kotlin.runCatching {
                                                    vm.billsRecognition(pageState.currentPage)
                                                }.onSuccess {
                                                    showDialog = true
                                                }.onFailure {
                                                    toast("识别失败：${it.message}")
                                                }
                                                isLoading = false
                                            }
                                        },
                                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onBackground),
                                    ) {
                                        Text(text = "识别")
                                    }
                                }
                            }
                        }) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            HorizontalPager(
                                state = pageState,
                            ) { page ->
                                painter = rememberAsyncImagePainter(
                                    vm.imageFiles[page].file
                                )
//
                                Image(
                                    painter = painter!!,
                                    contentDescription = null,
                                    contentScale = ContentScale.Inside,
                                    modifier = Modifier.fillMaxSize(),
                                )

                            }
                        }
                    }

                }
            }
        }
    }

//  scope.launch {
//                            kotlin.runCatching {
//                                vm.addBill(pageIndex)
//                            }.onSuccess {
//                                toast( "添加成功")
//                                onDismissRequest()
//                            }.onFailure { e ->
//                                if (e is NotLoggedInException) {
//                                    toast( "请先登录")
//                                }
//                                e.printStackTrace()
//                                toast( "添加失败")
//                            }
//                        }

}