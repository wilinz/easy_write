@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)

package com.eazywrite.app.ui.author

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.eazywrite.app.common.toast
import com.eazywrite.app.ui.theme.EazyWriteTheme
import com.eazywrite.app.util.setWindow
import com.eazywrite.app.R as R1



data class AuthorItem(
    val avatar: Int,
    val nickname: String,
    val qq: String,
    val github: String,
)

val codeAuthors = listOf(
    AuthorItem(
        avatar = R1.drawable.wilinz,
        nickname = "wilinz",
        qq = "3397733901",
        github = "https://github.com/wilinz"
    ),
    AuthorItem(
        avatar = R1.drawable.jxy,
        nickname = "jixiuy",
        qq = "2439923973",
        github = "https://github.com/jixiuy"
    ),
    AuthorItem(
        avatar = R1.drawable.hzw,
        nickname = "GuMu-Cat GuMu",
        qq = "1813211082",
        github = "https://github.com/GuMu-Cat"
    ),
    AuthorItem(
        avatar = R1.drawable.wkj,
        nickname = "KWJYES KjWei",
        qq = "1610017676",
        github = "https://github.com/KWJYES"
    ),
    AuthorItem(
        avatar = R1.drawable.xzt,
        nickname = "UI设计：Doggie",
        qq = "1078106886",
        github = ""
    )
)

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindow(isDarkStatusBarIcon = true)
        setContent {
            EazyWriteTheme {
                Surface(color = MaterialTheme.colorScheme.primary) {

                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(text = "作者")
                                },
                            )
                        }) {
                        Column(
                            modifier = Modifier
                                .padding(it)
                        ) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(24.dp,4.dp,24.dp,24.dp),
                                verticalArrangement = Arrangement.spacedBy(24.dp)
                            ){
                                itemsIndexed(codeAuthors){index, authorItem ->
                                    ElevatedCard(
                                        Modifier
                                            .fillMaxWidth()
//                                            .clip(RoundedCornerShape(16.dp))

                                            .padding()
                                    ) {
                                        val localClipboardManager = LocalClipboardManager.current
                                        val context = LocalContext.current
                                        Row(
                                            Modifier
                                                .clickable { }
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Image(
                                                modifier = Modifier
                                                    .size(48.dp)
                                                    .clip(RoundedCornerShape(50)),
                                                painter = painterResource(id = authorItem.avatar),
                                                contentDescription = "头像"
                                            )
                                            Spacer(modifier = Modifier.width(16.dp))
                                            Column(
                                                Modifier.weight(1f)
                                            ) {
                                                Text(text = authorItem.nickname, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.titleMedium)
                                                TextButton(onClick = {
                                                    localClipboardManager.setText(AnnotatedString(authorItem.qq))
                                                    toast("QQ号已经复制到剪切板")
                                                }) {
                                                    Text(text = "QQ: ${authorItem.qq}")
                                                }
                                                if (authorItem.github.isNotEmpty()) {
                                                    TextButton(onClick = {
                                                        val intent = Intent(Intent.ACTION_VIEW)
                                                        intent.data = Uri.parse(authorItem.github)
                                                        startActivity(intent)
                                                    }) {
                                                        Text(text = "Github: ${authorItem.github}")
                                                    }
                                                }

                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }


                }
            }
        }
    }
}