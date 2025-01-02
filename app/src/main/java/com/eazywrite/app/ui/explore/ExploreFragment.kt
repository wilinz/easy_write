package com.eazywrite.app.ui.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.eazywrite.app.ui.bill.ArticleActivity
import com.eazywrite.app.ui.bill.BillActivity
import com.eazywrite.app.ui.gpt.ChatPage
import com.eazywrite.app.ui.image_editing.CameraXActivity
import com.eazywrite.app.ui.theme.EazyWriteTheme
import com.eazywrite.app.ui.welcome.LoginActivity
import com.eazywrite.app.util.startActivity

class ExploreFragment @JvmOverloads constructor(
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
                    ChatPage()
                }
            }
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExplorePage(paddingValues: PaddingValues) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
                title = {
                    Text(text = "发现")
                }
            )
        }) {
        Column(
            Modifier
                .padding(it)
                .fillMaxSize()
                .padding(8.dp)
        ) {
            val context = LocalContext.current
            ElevatedButton(onClick = { context.startActivity<BillActivity>() }) {
                Text(text = "账单页面")
            }
            ElevatedButton(onClick = { context.startActivity<LoginActivity>() }) {
                Text(text = "欢迎页面")
            }
            ElevatedButton(onClick = { context.startActivity<CameraXActivity>() }) {
                Text(text = "相机预览")
            }
            ElevatedButton(onClick = { context.startActivity<com.eazywrite.app.ui.gpt.ChatActivity>() }) {
                Text(text = "ChatGPT聊天界面")
            }
            ElevatedButton(onClick = { context.startActivity<ArticleActivity>() }) {
                Text(text = "文章推送")
            }
        }
    }

}