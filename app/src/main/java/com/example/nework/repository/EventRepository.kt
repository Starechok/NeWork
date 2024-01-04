package com.example.nework.repository

import androidx.paging.PagingData
import com.example.nework.dto.Event
import com.example.nework.dto.Media
import com.example.nework.dto.MediaUpload
import com.example.nework.enums.AttachmentType
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    val data: Flow<PagingData<Event>>
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long)
    suspend fun unlikeById(id: Long)
    suspend fun save(event: Event)
    suspend fun saveWithAttachment(
        event: Event,
        upload: MediaUpload,
        type: AttachmentType,
    )
    suspend fun upload(upload: MediaUpload): Media
    suspend fun participate(id: Long)
    suspend fun doNotParticipate(id: Long)

}