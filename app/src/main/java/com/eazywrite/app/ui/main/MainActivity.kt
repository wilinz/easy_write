@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalFoundationApi::class, ExperimentalPermissionsApi::class
)

package com.eazywrite.app.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.StackedLineChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.viewpager2.widget.ViewPager2
import com.eazywrite.app.BuildConfig
import com.eazywrite.app.common.toast
import com.eazywrite.app.data.hideUpdateVersionCodeFlow
import com.eazywrite.app.data.model.AppVersionData
import com.eazywrite.app.service.BillService
import com.eazywrite.app.service.ServiceManager
import com.eazywrite.app.ui.profile.UpdateDialog
import com.eazywrite.app.ui.theme.EazyWriteTheme
import com.eazywrite.app.util.fillMaxSize
import com.eazywrite.app.util.setWindow
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

val LocalMainScreenScaffoldPaddingValues = staticCompositionLocalOf {
    PaddingValues(0.dp)
}

@OptIn(ExperimentalPermissionsApi::class)
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setWindow()
        setContent {
            EazyWriteTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CheckForUpdates()
                    MainPage()
                    LaunchedEffect(key1 = Unit, block = {
                        startNotificationService()
                    })
                }
            }
        }
    }

    @Composable
    private fun MainPage() {
        var currentPage by rememberSaveable {
            mutableStateOf(0)
        }
        Scaffold(
            bottomBar = {
                BottomBar(currentPage, onSelected = { currentPage = it })
            },
        ) { paddingValues ->
            CompositionLocalProvider(LocalMainScreenScaffoldPaddingValues provides paddingValues) {
                Box(
                    modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
                ) {
                    WindowInsets.ime
                    val parentView = LocalView.current
                    val localScaffoldPaddingValues = LocalMainScreenScaffoldPaddingValues.current
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { context ->
                            ViewPager2(context).apply {
                                adapter =
                                    ViewPage2ViewAdapter(parentView, localScaffoldPaddingValues)
                                registerOnPageChangeCallback(
                                    onViewPage2ChangeCallback(
                                        onPageSelected = { index ->
                                            currentPage = index
                                            setSystemUI(index)
                                        }
                                    )
                                )
                                isUserInputEnabled = false
                                offscreenPageLimit = 4
                            }.fillMaxSize()
                        },
                        update = { view ->
                            view.setCurrentItem(currentPage, false)
                        })

                }
            }
        }
    }


    @Composable
    private fun CheckForUpdates() {
        val context = LocalContext.current

        val vm = viewModel<MainViewModel>()
        val version by vm.appVersion.collectAsState(null)
        fun isHasNewVersion(versionCode: Long): Boolean {
            return versionCode > BuildConfig.VERSION_CODE
        }

        fun isForce(version: AppVersionData) =
            version.isForce && version.versionCode > BuildConfig.VERSION_CODE

        if (vm.isShowUpdateDialog && version?.versionCode?.let { isHasNewVersion(it) } == true) {
            version?.let {
                var hideVersionCode by remember{
                    mutableStateOf(it.versionCode)
                }
                if (!it.canHide){
                    hideVersionCode = 0L
                }
                LaunchedEffect(key1 = Unit, block = {
                    hideVersionCode = context.hideUpdateVersionCodeFlow.first()
                })
                if (hideVersionCode != it.versionCode || isForce(it)) {
                    UpdateDialog({ vm.isShowUpdateDialog = false }, it, context)
                }
            }
        }


        val scope = rememberCoroutineScope()
        fun checkUpdate(isBackground: Boolean) {
            scope.launch {
//                                            toast("正在检查更新")
                val version1 = kotlin.runCatching {
                    val data = vm.getAppVersion().data
                    vm.appVersion.value = data
                    data
                }.onFailure {
                    if (!isBackground) toast("检查更新失败：${it.stackTrace}")
                }.getOrNull()

                if (version1 != null) {
                    if (isHasNewVersion(version1.versionCode)) {
                        vm.isShowUpdateDialog = true
                    } else {
                        if (!isBackground) toast("已是最新版本")
                    }
                }


            }
        }

    }

    private fun startNotificationService() {
        ServiceManager.getInstance().bindService(
            this,
            false, null, BillService::class.java
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        ServiceManager.getInstance().unbindService(
            this,
            null, BillService::class.java
        )
    }

    fun setSystemUI(page: Int) {
        when (page) {
            0 -> setWindow(isDarkStatusBarIcon = false)
            1 -> setWindow(isDarkStatusBarIcon = true)
            2 -> setWindow(isDarkStatusBarIcon = true)
            else -> setWindow(isDarkStatusBarIcon = false)
        }
    }


    companion object {
        fun jumpMainActivity(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }
}

data class BottomItem(val title: String, val icon: ImageVector, val selectedIcon: ImageVector)

@Composable
private fun BottomBar(currentSelected: Int, onSelected: (index: Int) -> Unit) {
    val items = remember {
        mutableStateListOf(
            BottomItem("首页", Icons.Outlined.Home, Icons.Default.Home),
            BottomItem("统计", Icons.Outlined.StackedLineChart, Icons.Default.BarChart),
            BottomItem("发现", Icons.Outlined.Explore, Icons.Default.Explore),
            BottomItem("我的", Icons.Outlined.Person, Icons.Default.Person)
        )
    }
    NavigationBar {
        items.forEachIndexed { index, item ->
            val selected = currentSelected == index
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        onSelected(index)
                    }
                },
                label = {
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                icon = {
                    Icon(
                        if (selected) item.selectedIcon else item.icon,
                        contentDescription = item.title,
                    )
                },
//                colors = NavigationBarItemDefaults.colors(selectedIconColor =)
            )
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun BottomBarPreview() {
    EazyWriteTheme {
        BottomBar(currentSelected = 0, onSelected = {})
    }
}

fun onViewPage2ChangeCallback(
    onPageScrolled: ((
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int
    ) -> Unit)? = null,
    onPageSelected: ((position: Int) -> Unit)? = null,
    onPageScrollStateChanged: ((state: Int) -> Unit)? = null
): ViewPager2.OnPageChangeCallback {
    return object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            onPageScrolled?.invoke(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            onPageSelected?.invoke(position)
        }

        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            onPageScrollStateChanged?.invoke(state)
        }
    }
}