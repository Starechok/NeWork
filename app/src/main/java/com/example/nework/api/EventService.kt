package com.example.nework.api

import com.example.nework.dto.Event
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface EventService {
    @GET("events/{id}/before")
    suspend fun getBefore(
        @Path("id") id: Long,
        @Query("count") count: Int,
    ): Response<List<Event>>

    @GET("events/{id}/after")
    suspend fun getAfter(
        @Path("id") id: Long,
        @Query("count") count: Int,
    ): Response<List<Event>>

    @GET("events/latest")
    suspend fun getLatest(@Query("count") count: Int): Response<List<Event>>

    @POST("events")
    suspend fun save(@Body event: Event): Response<Event>

    @DELETE("events/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>

    @POST("events/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Event>

    @DELETE("events/{id}/likes")
    suspend fun unlikeById(@Path("id") id: Long): Response<Event>

    @POST("events/{id}/participants")
    suspend fun participate(@Path("id") id: Long): Response<Event>

    @DELETE("events/{id}/participants")
    suspend fun doNotParticipate(@Path("id") id: Long): Response<Event>
}