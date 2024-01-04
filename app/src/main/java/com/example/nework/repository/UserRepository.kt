package com.example.nework.repository

import com.example.nework.dto.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val data: Flow<List<User>>

    suspend fun getAll()

    suspend fun getUser(id: Long): User
}