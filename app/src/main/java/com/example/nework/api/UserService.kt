package com.example.nework.api

import com.example.nework.dto.Token
import com.example.nework.dto.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface UserService {
    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun login(
        @Field("login") login: String,
        @Field("password") pass: String,
    ): Response<Token>

    @Multipart
    @POST("users/registration")
    suspend fun registerUser(
        @Part("login") login: RequestBody,
        @Part("password") pass: RequestBody,
        @Part("name") name: RequestBody,
        @Part file: MultipartBody.Part?,
    ): Response<Token>

    @GET("users")
    suspend fun getUsers(): Response<List<User>>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Long): Response<User>
}