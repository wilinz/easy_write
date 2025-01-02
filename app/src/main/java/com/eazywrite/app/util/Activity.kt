package com.eazywrite.app.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

@JvmOverloads
fun Activity.setWindow(
    isDecorFitsSystemWindows: Boolean = false,
    isDarkStatusBarIcon: Boolean = false
) {
    WindowCompat.setDecorFitsSystemWindows(window, isDecorFitsSystemWindows)
    window.statusBarColor = Color.Transparent.toArgb()
    window.navigationBarColor = Color.Transparent.toArgb()
    WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
        isDarkStatusBarIcon
}

inline fun <reified T> Context.startActivity() {
    this.startActivity(
        Intent(
            this,
            T::class.java
        )
    )
}