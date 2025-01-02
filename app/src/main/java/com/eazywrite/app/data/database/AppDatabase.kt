package com.eazywrite.app.data.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase
import com.eazywrite.app.BuildConfig
import com.eazywrite.app.MyApplication
import com.eazywrite.app.data.model.Bill
import com.eazywrite.app.data.model.StringList
import com.eazywrite.app.data.model.User
import com.eazywrite.app.data.moshi
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.concurrent.Executors

@Database(
    entities = [Bill::class, User::class], version = 3,
    autoMigrations = [
        AutoMigration(
            from = 1,
            to = 2,
            spec = AppDatabase.MyExampleAutoMigration::class
        ),
        AutoMigration(
            from = 2,
            to = 3,
            spec = AppDatabase.MyExampleAutoMigration::class
        ),
    ],
    exportSchema = true
)
@TypeConverters(
    LocalDateTimeConverter::class,
    LocalDateConverter::class,
    BigDecimalConverter::class,
    StringListConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun billDao(): BillDao
    abstract fun userDao(): UserDao

    class MyExampleAutoMigration : AutoMigrationSpec {
        @Override
        override fun onPostMigrate(db: SupportSQLiteDatabase) {
            // Invoked once auto migration is done
        }
    }
}

val db = Room.databaseBuilder(
    MyApplication.instance,
    AppDatabase::class.java, "app_database"
).setQueryCallback(
    object : RoomDatabase.QueryCallback {
        override fun onQuery(sqlQuery: String, bindArgs: List<Any?>) {
            if (BuildConfig.DEBUG) {
//                Log.d("RoomDatabase", "SQL Query: $sqlQuery SQL Args: $bindArgs")
            }
        }
    },
    Executors.newSingleThreadExecutor()
)
    .build()


object LocalDateTimeConverter {
    @JvmStatic
    @TypeConverter
    fun toTimestamp(localDateTime: LocalDateTime?): Long? {
        return localDateTime?.toInstant(ZoneOffset.UTC)?.toEpochMilli()
    }

    @JvmStatic
    @TypeConverter
    fun toDateTime(timestamp: Long?): LocalDateTime? {
        return timestamp?.let {
            LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.of("UTC"))
        }
    }
}

object LocalDateConverter {
    @JvmStatic
    @TypeConverter
    fun toTimestamp(localDate: LocalDate?): Long? {
        return localDate?.atStartOfDay()?.toInstant(ZoneOffset.UTC)?.toEpochMilli()
    }

    @JvmStatic
    @TypeConverter
    fun toDate(timestamp: Long?): LocalDate? {
        return timestamp?.let {
            LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.of("UTC")).toLocalDate()
        }
    }
}

object BigDecimalConverter {
    @JvmStatic
    @TypeConverter
    fun toLong(bigDecimal: BigDecimal?): Long? {
        return bigDecimal?.multiply(100.toBigDecimal())?.toLong()
    }

    @JvmStatic
    @TypeConverter
    fun toBigDecimal(long: Long?): BigDecimal? {
        return long?.toBigDecimal()?.divide(100.toBigDecimal())
    }
}

object StringListConverter {
    @JvmStatic
    @TypeConverter
    fun toString(stringList: StringList?): String? {
        return stringList?.let {
            moshi.adapter(StringList::class.java).toJson(it)
        }
    }

    @JvmStatic
    @TypeConverter
    fun toStringList(json: String?): StringList? {
        return json?.let {
            kotlin.runCatching { moshi.adapter(StringList::class.java).fromJson(it) }.getOrNull()
        }
    }
}