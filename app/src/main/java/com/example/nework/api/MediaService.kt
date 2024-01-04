package com.example.nework.api

import com.example.nework.dto.Media
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MediaService {
    @Multipart
    @POST("media")
    suspend fun uploadMedia(@Part media: MultipartBody.Part): Response<Media>
}