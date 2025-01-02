@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.eazywrite.app.ui.importbill

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.eazywrite.app.R
import com.eazywrite.app.common.toast
import com.eazywrite.app.service.AutoAccessibilityService
import com.eazywrite.app.ui.theme.EazyWriteTheme
import com.eazywrite.app.util.*
import com.wilinz.accessbilityx.AccessibilityxService
import com.wilinz.accessbilityx.app.launchAppPackage
import com.wilinz.accessbilityx.bounds
import com.wilinz.accessbilityx.ensureClick
import com.wilinz.accessbilityx.text1
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class WeChatImportActivity : ComponentActivity() {

    companion object {
        private const val TAG = "WeChatImportActivity"
    }

    private val viewModel: WechatImportViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindow(isDarkStatusBarIcon = true)
        setData(intent)
        setContent {
            EazyWriteTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Content()
                }
            }
        }

    }

    private val autoService: AutoAccessibilityService?
        get() = AutoAccessibilityService.instance

    private val autoJob: CoroutineScope = MainScope()

    @Composable
    private fun Content() {
        Scaffold(topBar = {
            TopAppBar(
                title = { Text(text = "导入微信账单") }
            )
        }) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .verticalScroll(rememberScrollState())
            ) {
                FileInput(onFileInput = {
                    viewModel.uri = it
                })
                val context = LocalContext.current
                ElevatedButton(
                    onClick = {
                        context.packageManager.getLaunchIntentForPackage("com.tencent.mm")?.let {
                            context.startActivity(it)
                        } ?: kotlin.run {
                            toast(text = "未安装微信")
                        }
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "打开微信")
                }
                ElevatedButton(
                    onClick = {
                        autoJump(context)
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "打开微信（自动操作）")
                }
                Import(viewModel)
                if (viewModel.uri != null) {
                    val filename = remember(viewModel.uri) {
                        viewModel.uri!!.getFileName(this@WeChatImportActivity)
                            ?: getRandomName()
                    }
                    Text("已选择：$filename", modifier = Modifier.padding(8.dp))
                } else {
                    Text("未选择任何文件", modifier = Modifier.padding(16.dp))
                }
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                ) {
                    Text("微信导入说明：", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("使用QQ里面的QQ邮箱功能无法下载文件，所以需要使用QQ邮箱客户端, 账单文件只能下载一次，请勿多次点击")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("微信导入教程：", style = MaterialTheme.typography.titleLarge)

                }
                Image(
                    painter = painterResource(id = R.drawable.wechat_import),
                    modifier = Modifier.fillMaxWidth(),
                    contentDescription = "微信导入教程",
                    contentScale = ContentScale.FillWidth
                )
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Text("如何找到已下载的账单文件：", style = MaterialTheme.typography.titleLarge)
                }
                Image(
                    painter = painterResource(id = R.drawable.wechat_import2),
                    modifier = Modifier.fillMaxWidth(),
                    contentDescription = "如何找到已下载的账单文件",
                    contentScale = ContentScale.FillWidth
                )
            }
        }
    }

    private fun autoJump(context: Context) {
        if (autoService == null || !AccessibilityxService.isAccessibilityServiceEnabled(
                context,
                AutoAccessibilityService::class.java
            )
        ) {
            toast(
                "请在无障碍设置中打开“${context.getString(R.string.text_import_invoices_automatically)}”",
                duration = Toast.LENGTH_LONG
            )
            context.startActivity(
                Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                )
            )
            return
        }
        if (launchAppPackage("com.tencent.mm")) {
            autoJob.launch {
                listOf(
                    "我",
                    "服务",
                    "钱包",
                    "账单",
                    "常见问题",
                    "下载账单",
                    "用于个人对账"
                ).forEach { text ->
                    if (text == "下载账单") {
                        autoService?.untilFindOne { it.text1 == text }
                            ?.let { node ->
                                delay(200)
                                autoService?.click(node.bounds)
                            }
                    } else {
                        autoService?.untilFindOne { it.text1 == text }
                            ?.ensureClick()
                    }

                }
                val successFlag =
                    autoService?.untilFindOne { it.text1 == "申请已提交" }
                if (successFlag != null) {
                    toast("申请已提交")
                    autoService?.untilFindOne { it.text1 == "确定" }
                        ?.ensureClick()
                    autoService?.untilFindOne { it.text1 == "账单" }
                    for (i in 0 until 7) {
                        delay(500)
                        autoService?.back()
                    }
                    listOf("微信").forEach { text ->
                        autoService?.untilFindOne { it.text1 == text }
                            ?.ensureClick()
                    }
//                    autoService?.untilFindOne { it.text1 == "账单文件发送成功通知" }
//                        ?.let { node ->
//                            autoService?.click(node.bounds)
//                        }
                }
            }
        } else {
            toast(text = "未安装微信")
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d(TAG, "onNewIntent: ")
        intent?.let {
            setData(it)
        }
    }

    private fun setData(intent: Intent) {
        Log.d(TAG, "setData:action: ${intent.action}")
        Log.d(TAG, "setData:data: ${intent.data}")
        when (intent.action) {
            Intent.ACTION_SEND -> {
                intent.getParcelableExtraCompat<Uri>(Intent.EXTRA_STREAM)?.let {
                    viewModel.uri = it
                }
            }

            Intent.ACTION_VIEW -> {
                intent.data?.let {
                    viewModel.uri = it
                }
            }
        }
    }


}





