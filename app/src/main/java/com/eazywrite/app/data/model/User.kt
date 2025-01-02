package com.eazywrite.app.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "users", indices = [Index("username","is_active")])
data class User (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val nickname: String,
    val avatar: String,
    @ColumnInfo("is_active")
    val isActive: Boolean
)