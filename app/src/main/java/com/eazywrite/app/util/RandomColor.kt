package com.eazywrite.app.util

import androidx.compose.ui.graphics.Color
import java.util.*

fun getRandomColor(): Color {
    val rand = Random()
    return Color(
        alpha = 0xFF,
        red = rand.nextInt(0xFF),
        green = rand.nextInt(0xFF),
        blue = rand.nextInt(0xFF)
    )
}
