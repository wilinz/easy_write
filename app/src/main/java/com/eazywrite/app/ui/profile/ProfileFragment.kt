@file:OptIn(ExperimentalPermissionsApi::class)

package com.eazywrite.app.ui.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eazywrite.app.BuildConfig
import com.eazywrite.app.R
import com.eazywrite.app.common.toast
import com.eazywrite.app.data.hideUpdateVersionCodeFlow
import com.eazywrite.app.data.model.AppVersionData
import com.eazywrite.app.data.notificationPermission
import com.eazywrite.app.data.setHideUpdateVersionCode
import com.eazywrite.app.data.setNotificationPermission
import com.eazywrite.app.databinding.KefuBinding
import com.eazywrite.app.ui.FrequentlyAskedQuestionsActivity
import com.eazywrite.app.ui.author.SettingsActivity
import com.eazywrite.app.ui.profile.feedback.FeedbackActivity
import com.eazywrite.app.ui.welcome.LoginActivity
import com.eazywrite.app.ui.wiget.MarkdowmTextView
import com.eazywrite.app.util.startActivity
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage() {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
        }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Header(paddingValues = it)
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Card1()
                Spacer(modifier = Modifier.height(16.dp))
                Card2()
                Spacer(modifier = Modifier.height(16.dp))
                Logout()
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

}

@Composable
private fun Logout() {
    val context = LocalContext.current
    val vm = viewModel<ProfileViewModel>()
    val user by vm.getCurrentUserFlow().collectAsState(null)
    var showDialog by remember {
        mutableStateOf(false)
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(text = "退出登录")
            },
            text = {
                Text(text = "确定退出登录")
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.logout()
                    showDialog = false
                }) {
                    Text(text = "确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(text = "取消")
                }
            }
        )
    }
    ElevatedButton(
        onClick = {
            if (user != null) {
                showDialog = true
            } else {
                context.startActivity(Intent(context, LoginActivity::class.java))
            }
        },
        Modifier
            .padding(0.dp)
            .fillMaxWidth()
    ) {
        Text(text = if (user != null) "退出账号" else "登录")
    }
}


@Composable
fun Header(paddingValues: PaddingValues) {

    Box(
        modifier = Modifier,

        ) {
        Image(
            painter = painterResource(id = R.drawable.bill_background),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .height(152.dp)
                .fillMaxWidth()
        )
        val context = LocalContext.current

        val vm = viewModel<ProfileViewModel>()
        val user by vm.getCurrentUserFlow().collectAsState(initial = null)

        Card(
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding())
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0x33FEF5EE))

        ) {
            Box(modifier = Modifier
                .clickable {
                    context.startActivity<LoginActivity>()
                }
                .padding(16.dp)

            )
            {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        painter = painterResource(id = R.drawable.ic_launcher_playstore),
                        contentDescription = null
                    )
                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        Text(
                            text = user?.username ?: "用户登录",
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }


        }
    }
}

@Composable
fun Item(
    onClick: () -> Unit,
    icon: @Composable RowScope.() -> Unit,
    text: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                onClick()
            }
            .padding(start = 14.dp, end = 14.dp, top = 14.dp, bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Spacer(modifier = Modifier.width(16.dp))
        text()
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            Icons.Default.ExpandMore,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.rotate(-90f)
        )
    }
}

@Composable
fun Card1() {
    ElevatedCard {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp)
        ) {
            CustomerServiceNumber()
            Feedback()
            AppDetailsSettings()
            NotificationPermissions()
        }
    }
}

@Composable
fun Card2() {
    ElevatedCard {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp)
        ) {
            Author()
            FrequentlyAskedQuestions()
            DownloadAddress()
            ExitApp()
            CheckForUpdates()
        }
    }
}

@Composable
private fun CustomerServiceNumber() {
    val context = LocalContext.current
    Item(
        onClick = {
            if (context is AppCompatActivity) {
                val modalBottomSheet = MyBottomSheet()
                modalBottomSheet
                    .show(
                        context.supportFragmentManager,
                        "HelloWorld"
                    )
            }
        },
        icon = {
            Icon(
                Icons.Outlined.Call,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Text(text = "客服电话")
        }
    )
}

@Composable
private fun Author() {
    val context = LocalContext.current
    Item(
        onClick = {
            context.startActivity<SettingsActivity>()
        },
        icon = {
            Icon(
                Icons.Outlined.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Text(text = "作者相关")
        }
    )
}

@Composable
private fun FrequentlyAskedQuestions() {
    val context = LocalContext.current
    Item(
        onClick = {
            context.startActivity<FrequentlyAskedQuestionsActivity>()
        },
        icon = {
            Icon(
                Icons.Outlined.Feedback,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Text(text = "常见问题")
        }
    )
}

@Composable
private fun Feedback() {
    val context = LocalContext.current
    Item(
        onClick = {
            context.startActivity<FeedbackActivity>()
        },
        icon = {
            Icon(
                Icons.Outlined.Feedback,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Text(text = "问题反馈")
        }
    )
}

var isFirstTimeOpenAppDetailsSettings: Boolean = true

@Composable
private fun AppDetailsSettings() {
    val context = LocalContext.current
    var lastClickedTime by remember {
        mutableStateOf(0L)
    }
    Item(
        onClick = {
            if (System.currentTimeMillis() - lastClickedTime > (if (isFirstTimeOpenAppDetailsSettings) 3000 else 100)) {
                isFirstTimeOpenAppDetailsSettings = false
                lastClickedTime = System.currentTimeMillis()
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.data = Uri.fromParts("package", context.packageName, null)
                context.startActivity(intent)
            }
        },
        icon = {
            Icon(
                painterResource(id = R.drawable.settings),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Text(text = "应用详情")
        }
    )
}

@Composable
private fun ExitApp() {
    val context = LocalContext.current
    var lastExitClickedTime by remember {
        mutableStateOf(0L)
    }
    Item(
        onClick = {
            if (System.currentTimeMillis() - lastExitClickedTime < 2000) {
                (context as? Activity)?.finishAffinity()
                    ?: exitProcess(0)
            } else {
                lastExitClickedTime = System.currentTimeMillis()
                toast("再按一次退出")
            }
        },
        icon = {
            Icon(
                Icons.Outlined.PowerSettingsNew,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Text(text = "退出应用")
        }
    )
}

@Composable
private fun NotificationPermissions() {
    val context = LocalContext.current
    var showDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var isEnabled by rememberSaveable {
        mutableStateOf<Boolean?>(null)
    }
    LaunchedEffect(key1 = showDialog, block = {
        if (showDialog) {
            isEnabled = context.notificationPermission.first()
        }
    })
    val scope = rememberCoroutineScope()
    val notificationPermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS) { ok ->
                if (ok) {
                    scope.launch { context.setNotificationPermission(true) }
                }
            }
        } else {
            null
        }

    if (showDialog && isEnabled != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    if (isEnabled!!) {
                        scope.launch { context.setNotificationPermission(false) }
                    } else {
                        notificationPermission?.launchPermissionRequest() ?: run {
                            scope.launch { context.setNotificationPermission(true) }
                        }
                    }
                }) {
                    Text(text = if (isEnabled!!) "关闭" else "开启")
                }
            },
            title = {
                Text(text = "通知权限")
            },
            text = {
                Column(
                    Modifier
                        .height(64.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(text = if (isEnabled!!) "通知权限已开启，是否关闭" else "通知权限已关闭，是否开启，开启后可在通知栏快捷记账，App将保持运行")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(text = "取消")
                }
            }
        )
    }
    Item(
        onClick = {
            showDialog = true
        },
        icon = {
            Icon(
                Icons.Outlined.Notifications,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Text(text = "通知权限")
        }
    )
}

@Composable
private fun DownloadAddress() {
    val context = LocalContext.current
    var showDialog by rememberSaveable {
        mutableStateOf(false)
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data =
                        Uri.parse("https://home.wilinz.com:9992/share/AnWNLPcZ")
                    context.startActivity(intent)
                }) {
                    Text(text = "前往")
                }
            },
            title = {
                Text(text = "前往浏览器")
            },
            text = {
                Text(text = "是否前往浏览器")
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(text = "取消")
                }
            }
        )
    }
    Item(
        onClick = {
            showDialog = true
        },
        icon = {
            Icon(
                Icons.Outlined.GetApp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Text(text = "下载地址")
        }
    )
}

@Composable
private fun CheckForUpdates() {
    val context = LocalContext.current
    var showUpdateDialog by rememberSaveable {
        mutableStateOf(false)
    }

    val vm = viewModel<ProfileViewModel>()
    val version by vm.appVersion.collectAsState(null)
    if (showUpdateDialog) {
        version?.let {
            UpdateDialog({ showUpdateDialog = false }, it, context)
        }
    }

    fun isHasNewVersion(): Boolean {
        return (version?.versionCode
            ?: 0) > BuildConfig.VERSION_CODE
    }

    val scope = rememberCoroutineScope()
    fun checkUpdate(isBackground: Boolean) {
        scope.launch {
//                                            toast("正在检查更新")
            kotlin.runCatching {
                vm.appVersion.value = vm.getAppVersion().data
            }.onSuccess {
                if (isHasNewVersion()) {
                    showUpdateDialog = true
                } else {
                    if (!isBackground) toast("已是最新版本")
                }
            }.onFailure {
                if (!isBackground) toast("检查更新失败：${it.stackTrace}")
            }

        }
    }

    Item(
        onClick = {
            checkUpdate(false)
        },
        icon = {
            Icon(
                Icons.Outlined.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Text(
                text = if (isHasNewVersion())
                    "有新版本：${BuildConfig.VERSION_NAME} -> ${version?.versionName}"
                else "当前版本：${BuildConfig.VERSION_NAME}"
            )
        }
    )
}

@Composable
fun UpdateDialog(
    onDismissRequest: () -> Unit,
    version: AppVersionData,
    context: Context
) {
    val hideVersionCode by context.hideUpdateVersionCodeFlow.collectAsState(0L)
    var isHide by remember(hideVersionCode) {
        mutableStateOf(hideVersionCode == version.versionCode)
    }
    val scope = rememberCoroutineScope()
    fun isForce() = version.isForce && version.versionCode > BuildConfig.VERSION_CODE
    fun onDismissRequest1() {
        scope.launch { context.setHideUpdateVersionCode(if (isHide) version.versionCode else 0L) }
        onDismissRequest()
    }
    AlertDialog(
        onDismissRequest = { onDismissRequest1() },
        confirmButton = {
            TextButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(version.downloadUrl)
                    context.startActivity(intent)
                },
            ) {
                Text(text = "去下载")
            }
        },
        text = {
            Column {
                Text(text = "更新时间：${version.updatedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}")
                Spacer(modifier = Modifier.height(8.dp))
                MarkdowmTextView(text = version.changelog)
                Spacer(modifier = Modifier.height(8.dp))

                if (!isForce() && version.canHide) {
                    CheckboxWithLabel(
                        checked = isHide,
                        onCheckedChange = { isHide = it },
                        text = {
                            Text(text = "不再提示")
                        },
                    )
                }
            }
        },
        dismissButton = {
            if (!isForce()) {
                TextButton(onClick = { onDismissRequest1() }) {
                    Text(text = "关闭")
                }
            }
        },
        title = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Text(text = "新版本 ${version.versionName}")
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = !isForce(),
            dismissOnClickOutside = !isForce()
        )
    )
}

@Composable
fun CheckboxWithLabel(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    text: @Composable RowScope.() -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onCheckedChange?.invoke(!checked) }) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        text()
        Spacer(modifier = Modifier.width(12.dp))
    }
}

@Preview
@Composable
fun PreviewProfilePage() {

}

class MyBottomSheet : BottomSheetDialogFragment(), OnClickListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.kefu, container, false)

        return binding.root
    }

    lateinit var binding: KefuBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.bottomSheet.layoutParams.height = context?.display!!.height / 7
        }
        binding.telephone.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.telephone -> {
                var intent = Intent()
                intent.action = Intent.ACTION_DIAL
                intent.data = Uri.parse("tel:10086")
                startActivity(intent)
            }
        }
    }
}