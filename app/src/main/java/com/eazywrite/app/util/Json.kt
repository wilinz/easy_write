package com.eazywrite.app.util

private val pattern = """\{.*\}""".toRegex(RegexOption.DOT_MATCHES_ALL)

fun extractJsonFromString(string: String): String? {
    return pattern.find(string)?.value
}