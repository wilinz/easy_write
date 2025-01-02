@file:OptIn(ExperimentalMaterial3Api::class)

package com.eazywrite.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eazywrite.app.ui.theme.EazyWriteTheme
import com.eazywrite.app.ui.wiget.MarkdowmTextView
import com.eazywrite.app.util.setWindow
import com.tencent.bugly.crashreport.CrashReport

@OptIn(ExperimentalMaterial3Api::class)
class FrequentlyAskedQuestionsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindow(isDarkStatusBarIcon = true)
        setContent {
            EazyWriteTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(topBar = {
                        TopAppBar(
                            title = {
                                Text(text = "常见问题", modifier = Modifier.pointerInput(Unit) {
                                    detectTapGestures(onDoubleTap = {
                                        CrashReport.testJavaCrash();
                                    })
                                })
                            }
                        )
                    }) {
                        Column(
                            modifier = Modifier
                                .padding(it)
                                .fillMaxSize()
                                .verticalScroll(
                                    rememberScrollState()
                                )
                                .padding(16.dp)
                        ) {
                            MarkdowmTextView(
                                text = """
                                1. 显示网络异常  
                                解决方案：打开“我的”页面“应用详情”，查看是否已经允许App使用网络  
                                
                                2. 其他问题  
                                解决方案：打开“我的”页面“问题反馈”，向开发者反馈问题
                            """.trimIndent()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting2(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    EazyWriteTheme {
        Greeting2("Android")
    }
}