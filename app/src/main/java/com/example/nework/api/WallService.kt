package com.example.nework.api

import com.example.nework.dto.Post
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface WallService {

    @GET("{authorId}/wall")
    suspend fun getUserWall(
        @Path("authorId") authorId: Long
    ): Response<List<Post>>

}