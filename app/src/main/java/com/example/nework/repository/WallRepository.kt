package com.example.nework.repository

import com.example.nework.dto.MediaUpload
import com.example.nework.dto.Post
import com.example.nework.enums.AttachmentType
import kotlinx.coroutines.flow.Flow

interface WallRepository {
    val data: Flow<List<Post>>
    suspend fun getUserWall(userId: Long)
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long)
    suspend fun unlikeById(id: Long)
    suspend fun save(post: Post)
    suspend fun saveWithAttachment(
        post: Post,
        upload: MediaUpload,
        type: AttachmentType,
    )
}