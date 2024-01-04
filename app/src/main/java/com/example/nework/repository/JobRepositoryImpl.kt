package com.example.nework.repository

import com.example.nework.api.JobService
import com.example.nework.dao.JobDao
import com.example.nework.dto.Job
import com.example.nework.entity.JobEntity
import com.example.nework.entity.toDto
import com.example.nework.entity.toJobEntity
import com.example.nework.error.ApiError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class JobRepositoryImpl @Inject constructor(

    private val dao: JobDao,
    private val apiService: JobService
) : JobRepository {
    override val data: Flow<List<Job>> = dao.getAll()
        .map { it.toDto() }
        .flowOn(Dispatchers.Default)

    override suspend fun getUserJobs(id: Long) {
        dao.deleteAll()
        val response = apiService.getUserJobs(id)
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        val body = response.body() ?: throw ApiError(response.code(), response.message())
        dao.insert(body.toJobEntity())
    }

    override suspend fun save(job: Job) {
        val response = apiService.saveJob(job)
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        val body = response.body() ?: throw ApiError(response.code(), response.message())
        dao.insert(JobEntity.fromDto(body))
    }

    override suspend fun removeById(id: Long) {
        val response = apiService.removeById(id)
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        dao.removeById(id)
    }
}