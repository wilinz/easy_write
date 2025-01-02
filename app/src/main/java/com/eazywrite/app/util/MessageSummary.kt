package com.eazywrite.app.util

import java.math.BigInteger
import java.security.MessageDigest
import kotlin.text.Charsets.UTF_8


fun messageSummary(input: String, algorithm: String) =
    MessageDigest.getInstance(algorithm).digest(input.toByteArray(UTF_8)).toHex()

private fun ByteArray.toHex() = joinToString(separator = "") { byte -> "%02x".format(byte) }