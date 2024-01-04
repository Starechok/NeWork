package com.example.nework.repository


import com.example.nework.api.UserService
import com.example.nework.dao.UserDao
import com.example.nework.dto.User
import com.example.nework.entity.toDto
import com.example.nework.entity.toUserEntity
import com.example.nework.error.ApiError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userService: UserService,
    private val userDao: UserDao,
) :
    UserRepository {
    override val data: Flow<List<User>> =
        userDao.getAll()
            .map { it.toDto() }
            .flowOn(Dispatchers.Default)

    override suspend fun getAll() {
        val response = userService.getUsers()
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        val body = response.body() ?: throw ApiError(response.code(), response.message())
        userDao.insert(body.toUserEntity())
    }

    override suspend fun getUser(id: Long): User {
        val response = userService.getUserById(id)
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        return response.body() ?: throw ApiError(response.code(), response.message())
    }
}