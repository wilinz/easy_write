package com.eazywrite.app.util

import java.math.BigDecimal
import java.math.RoundingMode

const val TAG = "BigDecimal.kt"

fun BigDecimal.secureDivide(other: BigDecimal, scale: Int): BigDecimal{
    if (other == 0.toBigDecimal()) return BigDecimal.ZERO
    return this.divide(other,scale, RoundingMode.HALF_UP)
}