package com.eazywrite.app.util

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import java.lang.reflect.Field

fun <T : View> T.fillMaxSize() = this.apply {
    layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
}

fun ComposeView.applyContent(content: @Composable () -> Unit) = this.apply {
    setContent(content)
}

@SuppressLint("DiscouragedPrivateApi")
fun TextView.setTextSelectHandle(
    textSelect: Drawable,
    textSelectLeft: Drawable,
    textSelectRight: Drawable
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        setTextSelectHandle(textSelect)
        setTextSelectHandleLeft(textSelectLeft)
        setTextSelectHandleRight(textSelectRight)
    } else {
        try {
            val fEditor: Field = TextView::class.java.getDeclaredField("mEditor")
            fEditor.isAccessible = true
            val editor: Any = fEditor.get(this)!!
            val fSelectHandleLeft: Field =
                editor.javaClass.getDeclaredField("mSelectHandleLeft")
            val fSelectHandleRight: Field =
                editor.javaClass.getDeclaredField("mSelectHandleRight")
            val fSelectHandleCenter: Field =
                editor.javaClass.getDeclaredField("mSelectHandleCenter")
            fSelectHandleLeft.isAccessible = true
            fSelectHandleRight.isAccessible = true
            fSelectHandleCenter.isAccessible = true
            fSelectHandleLeft.set(editor, textSelectLeft)
            fSelectHandleRight.set(editor, textSelectRight)
            fSelectHandleCenter.set(editor, textSelect)
        } catch (ignored: Exception) {
        }
    }
}