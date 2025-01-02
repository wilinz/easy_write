package com.eazywrite.app.data.database

import androidx.room.*
import com.eazywrite.app.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    suspend fun getAll(): List<User>

    @Query("SELECT * FROM users WHERE is_active = 1 LIMIT 1")
    suspend fun getActive(): User?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getByName(username: String): User?

    @Query("UPDATE users SET is_active = 0 WHERE is_active = 1")
    suspend fun logoutAll(): Int

    @Query("SELECT * FROM users WHERE is_active = 1 LIMIT 1")
    fun getActiveFlow(): Flow<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg users: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(users: User)

    @Delete
    suspend fun delete(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(users: User)
}