package com.example.nework.api

import com.example.nework.dto.Job
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface JobService {
    @GET("{id}/jobs")
    suspend fun getUserJobs(@Path("id") id: Long): Response<List<Job>>

    @POST("my/jobs")
    suspend fun saveJob(@Body job: Job): Response<Job>

    @DELETE("my/jobs/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>
}