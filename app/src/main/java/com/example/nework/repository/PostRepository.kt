package com.example.nework.repository

import androidx.paging.PagingData
import com.example.nework.dto.MediaUpload
import com.example.nework.dto.Post
import com.example.nework.enums.AttachmentType
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    val data: Flow<PagingData<Post>>
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