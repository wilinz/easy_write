package com.eazywrite.app.data.model

import androidx.room.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(
    tableName = "bills",
    indices = [Index("username", "cloud_id"), Index("third_party_id", unique = true)]
)
@JsonClass(generateAdapter = true)
data class Bill(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("local_id")
    @Json(name = "local_id")
    var localId: Int = 0,

    @ColumnInfo("cloud_id")
    @Json(name = "id")
    var cloudId: Long = 0,

    @Json(name = "third_party_id")
    @ColumnInfo("third_party_id")
    var thirdPartyId: String? = "",

    var username: String = "",

    @Json(name = "amount")
    var amount: BigDecimal,

    @Json(name = "comment")
    var comment: String,

    @Json(name = "datetime")
    var datetime: LocalDateTime,

    @Json(name = "date")
    var date: LocalDate = datetime.toLocalDate(),

    @Json(name = "category")
    var category: String,

    @ColumnInfo(name = "transaction_partner")
    @Json(name = "transaction_partner")
    var transactionPartner: String = "",

    @Json(name = "name")
    var name: String = "",

    @Json(name = "type")
    var type: String = "",

    @ColumnInfo("created_at")
    @Json(name = "created_at")
    var createdAt: LocalDateTime? = LocalDateTime.now(),

    @ColumnInfo(name = "updated_at")
    @Json(name = "updated_at")
    var updatedAt: LocalDateTime? = LocalDateTime.now(),

    @ColumnInfo(name = "deleted_at")
    @Json(name = "deleted_at")
    var deletedAt: LocalDateTime? = null,

    @ColumnInfo(name = "is_synced")
    @Json(ignore = true)
    var isSynced: Boolean? = null,

    @ColumnInfo(name = "images_comment")
    @Json(name = "images_comment")
    var imagesComment: StringList? = null,

) {
    companion object {
        const val TYPE_IN = "in"
        const val TYPE_OUT = "out"
    }
}

