package com.eazywrite.app.data.model

class StringList : ArrayList<String>() {
    companion object {
        fun of(list: List<String>): StringList {
            return StringList().apply { addAll(list) }
        }
    }
}