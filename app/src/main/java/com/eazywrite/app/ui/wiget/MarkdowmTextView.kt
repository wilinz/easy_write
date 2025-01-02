package com.eazywrite.app.ui.wiget

import androidx.appcompat.widget.AppCompatTextView
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.eazywrite.app.R
import com.eazywrite.app.util.lightenColor
import com.eazywrite.app.util.setTextSelectHandle
import io.noties.markwon.Markwon

@Composable
fun MarkdowmTextView(text: String, fontSize : TextUnit = MaterialTheme.typography.bodyMedium.fontSize){
    val context = LocalContext.current
    val primary = MaterialTheme.colorScheme.primary
    AndroidView(
        factory = {
            val selectContentColor = lightenColor(primary, 0.35f)
            val textSelect =
                ContextCompat.getDrawable(context, R.drawable.text_select)!!
                    .apply { setTint(selectContentColor.toArgb()) }
            val textSelectLeft =
                ContextCompat.getDrawable(context, R.drawable.text_select_left)!!
                    .apply { setTint(primary.toArgb()) }
            val textSelectRight =
                ContextCompat.getDrawable(context, R.drawable.text_select_right)!!
                    .apply { setTint(primary.toArgb()) }
            AppCompatTextView(it).apply {
                setTextIsSelectable(true)
                setTextSelectHandle(textSelect, textSelectLeft, textSelectRight)
                highlightColor = selectContentColor.copy(alpha = 0.75f).toArgb()
                textSize = fontSize.value
            }
        },
        update = {
            it.apply {
                val markwon = Markwon.create(context);
                markwon.setMarkdown(this, text);
            }
        }
    )
}