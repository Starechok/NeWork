package com.example.nework.repository

import com.example.nework.dto.Job
import kotlinx.coroutines.flow.Flow

interface JobRepository {
    val data: Flow<List<Job>>

    suspend fun getUserJobs(id: Long)
    suspend fun save(job: Job)
    suspend fun removeById(id: Long)
}