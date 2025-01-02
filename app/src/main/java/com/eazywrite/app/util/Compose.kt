package com.eazywrite.app.util

import androidx.compose.ui.unit.LayoutDirection

fun LayoutDirection.reverse() =
    if (this == LayoutDirection.Ltr) LayoutDirection.Rtl else LayoutDirection.Ltr
