package com.eazywrite.app.data.repository

import com.eazywrite.app.data.database.db
import com.eazywrite.app.data.model.User
import com.eazywrite.app.data.network.Network
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.flow.Flow

object UserRepository {

    suspend fun getCurrentUser(): User? {
        return db.userDao().getActive()
    }

    fun getCurrentUserFlow(): Flow<User?> {
        return db.userDao().getActiveFlow()
    }

    suspend fun login(username: String) {
        db.userDao().logoutAll()
        val user = db.userDao().getByName(username)
        if (user != null) {
            db.userDao().update(user.copy(isActive = true))
            return
        }

        db.userDao().insert(
            User(
                username = username,
                nickname = "",
                avatar = "",
                isActive = true
            )
        )
        BillRepository.syncBills()
    }

    suspend fun logout() {
        val resp = Network.accountServiceKt.logout()
        when (resp.code) {
            200 -> {
                db.userDao().logoutAll()
            }
            10006 -> {
                throw Exception("您未登录")
            }
            else -> {
                throw Exception(resp.msg)
            }
        }
    }

    suspend fun logoutLocal() {
        db.userDao().logoutAll()
    }

}

class NotLoggedInException : IOException("请登录")