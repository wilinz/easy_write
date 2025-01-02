@file:OptIn(ExperimentalMaterial3Api::class, BetaOpenAI::class)

package com.eazywrite.app.ui.gpt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.eazywrite.app.R
import com.eazywrite.app.data.setMaxChatContextSize
import com.eazywrite.app.ui.main.LocalMainScreenScaffoldPaddingValues
import com.eazywrite.app.ui.theme.EazyWriteTheme
import com.eazywrite.app.ui.wiget.MarkdowmTextView
import com.eazywrite.app.util.reverse
import com.eazywrite.app.util.setWindow
import kotlinx.coroutines.launch
import kotlin.math.max


class ChatActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindow(isDarkStatusBarIcon = true)
        setContent {
            EazyWriteTheme() {
                Surface(color = MaterialTheme.colorScheme.background) {
                    ChatPage()
                }
            }
        }
    }

}

@Composable
fun ChatPage() {
    val vm: ChatViewModel = viewModel()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "发现") },
//                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                actions = {
                    MaxContextNumber(vm)
                    Clear(vm)
                }
            )
        },
    ) { paddingValues ->
        Box(modifier = Modifier.padding(top = paddingValues.calculateTopPadding())) {
            Column {
                val scrollState = rememberLazyListState()
                LazyColumn(modifier = Modifier.weight(1f), state = scrollState) {
                    itemsIndexed(vm.messages) { index, item ->
                        val rawLayoutDirection = LocalLayoutDirection.current
                        val layoutDirection =
                            if (item.role == ChatRole.User) rawLayoutDirection.reverse() else rawLayoutDirection
                        CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                            ) {
                                MessageItem(item)
                            }
                        }

                    }
                }

                LaunchedEffect(key1 = vm.messages.size, block = {
                    scrollState.animateScrollToItem(vm.messages.lastIndex)
                })

                val imePadding = WindowInsets.ime
                    .asPaddingValues()
                    .calculateBottomPadding()

                val scaffoldBottomPadding =
                    LocalMainScreenScaffoldPaddingValues.current.calculateBottomPadding()

                val imePaddingMinusBottomBar = imePadding - scaffoldBottomPadding

                OutlinedTextField(value = vm.inputMessage,
                    onValueChange = { vm.inputMessage = it },
                    modifier = Modifier
                        .padding(16.dp)
                        .padding(bottom = if (imePaddingMinusBottomBar > 0.dp) imePaddingMinusBottomBar else imePadding)
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                vm.viewModelScope.launch {
                                    vm.chat()
                                }
                            },
                            enabled = vm.inputMessage.isNotBlank()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "发送"
                            )
                        }
                    },
                    maxLines = 8,
                    label = {
                        Text(text = "请输入消息")
                    }
                )
            }
        }
    }
}

@Composable
private fun MaxContextNumber(vm: ChatViewModel) {
    var showDeleteDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var value by rememberSaveable {
        mutableStateOf(vm.maxContextNumber.value.toString())
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    if (showDeleteDialog) {
        AlertDialog(onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = "最大携带历史消息数量") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(text = "在与机器人聊天时，需要携带历史聊天记录，使得机器人能结合历史聊天记录进行回答，此数值如果过大可能会超出机器人的限制而导致聊天失败")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = value,
                        onValueChange = {
                            value = it
                        },
                        shape = MaterialTheme.shapes.medium,
                        label = { Text(text = "最大携带历史消息数量") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        value.toIntOrNull()?.let { n ->
                            scope.launch {
                                context.setMaxChatContextSize(max(n, 1))
                            }
                        }
                    },
                    enabled = (value.toIntOrNull() ?: 0) > 0
                ) {
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
    TextButton(
        onClick = {
            showDeleteDialog = true
        },
        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
    ) {
        Text(
            text = "最大历史记录 " + vm.maxContextNumber.collectAsState().value.toString(),
            style = MaterialTheme.typography.titleMedium,
        )
        Icon(
            imageVector = Icons.Default.ExpandMore,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun Clear(vm: ChatViewModel) {
    var showDeleteDialog by rememberSaveable {
        mutableStateOf(false)
    }
    if (showDeleteDialog) {
        AlertDialog(onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = "确定清空消息") },
            text = { Text(text = "清空消息") },
            confirmButton = {
                TextButton(onClick = {
                    if (!vm.isDisabledDeleteMessage) {
                        showDeleteDialog = false
                        vm.messages.apply {
                            clear()
                            add(welcomeMessage)
                        }
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
    IconButton(
        onClick = {
            showDeleteDialog = true
        },
        enabled = !vm.isDisabledDeleteMessage
    ) {
        Icon(painterResource(id = R.drawable.delete_xml), contentDescription = "清空")
    }
}

@Composable
private fun MessageItem(item: ChatMessage) {
    Image(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .size(48.dp),
        painter = painterResource(id = R.drawable.ic_launcher_playstore),
        contentDescription = "头像"
    )
    Spacer(modifier = Modifier.width(16.dp))
    ElevatedCard {
        val rawLayoutDirection = LocalLayoutDirection.current
        val layoutDirection =
            if (item.role == ChatRole.User) rawLayoutDirection.reverse() else rawLayoutDirection
        CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
            Box(modifier = Modifier.padding(12.dp)) {
//                SelectionContainer() {
//                    Text(text = item.content)
//                }
                val text = item.content ?: ""
                MarkdowmTextView(
                    text = text.ifEmpty { "..." },
                    MaterialTheme.typography.bodyLarge.fontSize
                )
            }
        }
    }
}

