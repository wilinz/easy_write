package com.eazywrite.app.data.database

import androidx.paging.PagingSource
import androidx.room.*
import com.eazywrite.app.data.model.Bill
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.time.LocalDate

@Dao
interface BillDao {

    @Query("SELECT bills.* FROM bills INNER JOIN users ON bills.username = users.username WHERE users.is_active = 1 AND bills.deleted_at  IS NULL ")
    suspend fun getAll(): List<Bill>

    @Query("SELECT bills.local_id FROM bills INNER JOIN users ON bills.username = users.username WHERE users.is_active = 1 AND bills.deleted_at  IS NULL ")
    fun getAllId(): Flow<List<Int>>

    @Query("""SELECT bills.* FROM bills INNER JOIN users ON bills.username = users.username
        WHERE users.is_active = 1
        AND bills.deleted_at IS NULL """)
    fun getAllFlow(): Flow<List<Bill>>

    @Query("""SELECT bills.* FROM bills INNER JOIN users ON bills.username = users.username
        WHERE users.is_active = 1
        AND (:startDate IS NULL OR bills.date >= :startDate) 
        AND (:endDate IS NULL OR bills.date <= :endDate) 
        AND (:category IS NULL OR bills.category = :category) 
        AND (:type IS NULL OR bills.type = :type) 
        AND bills.deleted_at  IS NULL 
        ORDER BY bills.datetime DESC""")
    fun getAllPaging(
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
        category: String? = null,
        type: String? = null,
    ): PagingSource<Int, Bill>

    @Query("""SELECT bills.* FROM bills INNER JOIN users ON bills.username = users.username
        WHERE users.is_active = 1
        AND bills.deleted_at  IS NULL 
        ORDER BY bills.datetime DESC LIMIT :limit""")
    suspend fun getRecent(limit: Int): List<Bill>

    @Query("""SELECT bills.* FROM bills INNER JOIN users ON bills.username = users.username
        WHERE users.is_active = 1
        AND bills.deleted_at  IS NULL 
        ORDER BY bills.datetime DESC LIMIT :limit""")
    fun getRecentFlow(limit: Int): Flow<List<Bill>>

    @Query("DELETE FROM bills WHERE username = :username AND (is_synced = 1 OR is_synced IS NULL)")
    suspend fun deleteOld(username: String): Int

    @Query("SELECT DISTINCT bills.date FROM bills INNER JOIN users ON bills.username = users.username WHERE users.is_active = 1 AND bills.deleted_at  IS NULL ORDER BY bills.datetime DESC")
    fun getAllDate(): PagingSource<Int, LocalDate>

    @Query("""SELECT DISTINCT bills.category FROM bills INNER JOIN users ON bills.username = users.username
        WHERE users.is_active = 1
        AND (:type IS NULL OR bills.type = :type)
        AND bills.deleted_at IS NULL
        ORDER BY bills.datetime DESC""")
    suspend fun getAllCategories(type: String? = null): List<String>

    @Query(
        """SELECT DISTINCT bills.date FROM bills INNER JOIN users ON bills.username = users.username 
        WHERE users.is_active = 1 
        AND (:startDate IS NULL OR bills.date >= :startDate) 
        AND (:endDate IS NULL OR bills.date <= :endDate) 
        AND (:category IS NULL OR bills.category = :category)
        AND bills.deleted_at  IS NULL 
        ORDER BY bills.datetime DESC
        LIMIT :limit"""
    )
    fun getAllDateFlow(
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
        category: String? = null,
        limit: Int = 20
    ): Flow<List<LocalDate>>

    @Query(
        """SELECT DISTINCT bills.category FROM bills INNER JOIN users ON bills.username = users.username 
        WHERE users.is_active = 1 
        AND (:startDate IS NULL OR bills.date >= :startDate) 
        AND (:endDate IS NULL OR bills.date <= :endDate) 
        AND type = :type
        AND bills.deleted_at IS NULL 
        ORDER BY bills.datetime DESC"""
    )
    suspend fun getAllCategory(
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
        type: String
    ): List<String>

    @Query(
        """SELECT SUM(bills.amount) FROM bills INNER JOIN users ON bills.username = users.username 
        WHERE users.is_active = 1 
        AND (:startDate IS NULL OR bills.date >= :startDate) 
        AND (:endDate IS NULL OR bills.date <= :endDate) 
        AND (:category IS NULL OR bills.category = :category)
        AND type = :type
        AND bills.deleted_at  IS NULL 
        """
    )
    fun getTotalAmountFlow(
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
        category: String? = null,
        type: String,
    ): Flow<BigDecimal?>

    @Query(
        """SELECT Count(bills.local_id) FROM bills INNER JOIN users ON bills.username = users.username 
        WHERE users.is_active = 1 
        AND (:startDate IS NULL OR bills.date >= :startDate) 
        AND (:endDate IS NULL OR bills.date <= :endDate) 
        AND (:category IS NULL OR bills.category = :category)
        AND (:type IS NULL OR bills.type = :type)
        AND bills.deleted_at  IS NULL 
        """
    )
    fun countBillFlow(
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
        category: String? = null,
        type: String? = null,
    ): Flow<Int>

    @Query(
        """SELECT SUM(bills.amount) FROM bills INNER JOIN users ON bills.username = users.username 
        WHERE users.is_active = 1 
        AND (:startDate IS NULL OR bills.date >= :startDate) 
        AND (:endDate IS NULL OR bills.date < :endDate) 
        AND (:category IS NULL OR bills.category = :category)
        AND type = :type
        AND bills.deleted_at  IS NULL 
        """
    )
    suspend fun getTotalAmount(
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
        category: String? = null,
        type: String,
    ): BigDecimal?

    @Query("SELECT MAX(bills.date) FROM bills INNER JOIN users ON bills.username = users.username WHERE users.is_active = 1 AND bills.deleted_at  IS NULL order by bills.date desc LIMIT 1")
    suspend fun getMaxDate(): LocalDate?

    @Query("SELECT bills.* FROM bills INNER JOIN users ON bills.username = users.username WHERE bills.date = :date AND users.is_active = 1  AND bills.deleted_at  IS NULL order by bills.date desc")
    fun getAllByDate(date: LocalDate): Flow<List<Bill>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(bills: List<Bill>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bill: Bill)

    @Delete
    suspend fun delete(bill: Bill): Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(bill: Bill): Int
}