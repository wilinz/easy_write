package com.eazywrite.app.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils


fun lightenColor(color: Color, fraction: Float): Color {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(color.toArgb(), hsl)
    hsl[1] *= 1 - fraction
    hsl[2] += (1 - hsl[2]) * fraction
    return Color.hsl(hsl[0], hsl[1], hsl[2], alpha = color.alpha)
}

fun Color.toArgbHex(): String {
    return String.format("#%08X", this.toArgb())
}

fun Color.toRgbHex(): String {
    return String.format("#%06X", (0xFFFFFF and this.toArgb()))
}
