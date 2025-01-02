package com.eazywrite.app.data

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

val localDateTimeDeserializer = JsonDeserializer { json, typeOfT, context ->
    LocalDateTime.ofInstant(
        Instant.ofEpochMilli(json?.asJsonPrimitive?.asLong ?: 0),
        ZoneId.systemDefault()
    )
}

val LocalDateTimeSerializer = JsonSerializer<LocalDateTime> { src, typeOfSrc, context ->
    JsonPrimitive(src.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
}

val gson = GsonBuilder()
    .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer)
    .registerTypeAdapter(LocalDateTime::class.java, localDateTimeDeserializer)
    .create()