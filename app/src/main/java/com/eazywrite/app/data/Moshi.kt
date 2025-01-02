package com.eazywrite.app.data

import com.eazywrite.app.data.model.StringList
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

object LocalDateTimeAdapter {

    @ToJson
    fun toJson(dateTime: LocalDateTime): Long {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    @FromJson
    fun fromJson(millis: Long): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
    }

}

object LocalDateTimeAdapter1 {

    @ToJson
    fun toJson(dateTime: LocalDateTime): String {
        return dateTime.toString()
    }

    @FromJson
    fun fromJson(str: String): LocalDateTime {
        return LocalDateTime.parse(str)
    }

}

object LocalDateAdapter {

    @ToJson
    fun toJson(dateTime: LocalDate): Long {
        return dateTime.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    @FromJson
    fun fromJson(millis: Long): LocalDate {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
            .toLocalDate()
    }

}

object LocalDateAdapter1 {

    @ToJson
    fun toJson(dateTime: LocalDate): String {
        return dateTime.toString()
    }

    @FromJson
    fun fromJson(str: String): LocalDate {
        return LocalDateTime.parse(str).toLocalDate()
    }

}

object LocalDateTimeNullableAdapter {

    @ToJson
    fun toJson(dateTime: LocalDateTime?): Long {
        return dateTime?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli() ?: 0
    }

    @FromJson
    fun fromJson(millis: Long): LocalDateTime? {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
    }

}

object LocalDateNullableAdapter {

    @ToJson
    fun toJson(dateTime: LocalDate?): Long {
        return dateTime?.atStartOfDay()?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
            ?: 0
    }

    @FromJson
    fun fromJson(millis: Long): LocalDate? {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
            .toLocalDate()
    }

}

object BigDecimalConverter1 {
    @ToJson
    fun toLong(bigDecimal: BigDecimal): String {
        return bigDecimal.toString()
    }

    @FromJson
    fun toBigDecimal(str: String): BigDecimal {
        return str.toBigDecimal()
    }

}

object BigDecimalConverter {
    @ToJson
    fun toLong(bigDecimal: BigDecimal): Long {
        return bigDecimal.multiply(100.toBigDecimal()).toLong()
    }

    @FromJson
    fun toBigDecimal(long: Long): BigDecimal {
        return long.toBigDecimal().divide(100.toBigDecimal())
    }

}

class StringListAdapter : JsonAdapter<StringList>() {
    @FromJson
    override fun fromJson(reader: JsonReader): StringList {
        val stringList = StringList()
        reader.beginArray()
        while (reader.hasNext()) {
            val item = reader.nextString()
            stringList.add(item)
        }
        reader.endArray()
        return stringList
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: StringList?) {
        if (value == null) {
            writer.nullValue()
            return
        }
        writer.beginArray()
        for (item in value) {
            writer.value(item)
        }
        writer.endArray()
    }
}


val moshi: Moshi = Moshi.Builder()
    .add(StringListAdapter())
    .add(KotlinJsonAdapterFactory())
    .add(LocalDateTimeAdapter)
    .add(LocalDateAdapter)
    .add(LocalDateTimeNullableAdapter)
    .add(LocalDateNullableAdapter)
    .add(BigDecimalConverter)
//    .add(StringListConverter)
    .build()

val moshi1: Moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .add(LocalDateTimeAdapter1)
    .add(LocalDateAdapter1)
    .add(BigDecimalConverter1)
//    .add(StringListConverter)
    .build()