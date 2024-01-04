package com.example.nework.repository


import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.nework.api.EventService
import com.example.nework.api.MediaService
import com.example.nework.dao.EventDao
import com.example.nework.dao.EventRemoteKeyDao
import com.example.nework.db.AppDb
import com.example.nework.dto.Attachment
import com.example.nework.dto.Event
import com.example.nework.dto.Media
import com.example.nework.dto.MediaUpload
import com.example.nework.entity.EventEntity
import com.example.nework.enums.AttachmentType
import com.example.nework.error.ApiError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepositoryImpl @Inject constructor(
    private val eventService: EventService,
    private val mediaService: MediaService,
    db: AppDb,
    private val eventDao: EventDao,
    eventRemoteKeyDao: EventRemoteKeyDao,
) :
    EventRepository {
    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<Event>> = Pager(
        config = PagingConfig(pageSize = 5),
        remoteMediator = EventRemoteMediator(eventService, db, eventDao, eventRemoteKeyDao),
        pagingSourceFactory = eventDao::pagingSource,
    ).flow.map { pagingData ->
        pagingData.map(EventEntity::toDto)
    }

    override suspend fun removeById(id: Long) {
        eventDao.removeById(id)
        val response = eventService.removeById(id)
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
    }

    override suspend fun likeById(id: Long) {
        eventDao.likeById(id)
        val response = eventService.likeById(id)
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        val body = response.body() ?: throw ApiError(response.code(), response.message())
        eventDao.insert(EventEntity.fromDto(body))
    }

    override suspend fun unlikeById(id: Long) {
        eventDao.unlikeById(id)
        val response = eventService.unlikeById(id)
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        val body = response.body() ?: throw ApiError(response.code(), response.message())
        eventDao.insert(EventEntity.fromDto(body))
    }

    override suspend fun save(event: Event) {
        val response = eventService.save(event)
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        val body = response.body() ?: throw ApiError(response.code(), response.message())
        eventDao.insert(EventEntity.fromDto(body))
    }

    override suspend fun saveWithAttachment(event: Event, upload: MediaUpload, type: AttachmentType) {
        val media = upload(upload)
        val postWithAttachment =
            event.copy(attachment = Attachment(media.url, type))
        val response = eventService.save(postWithAttachment)
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        val body = response.body() ?: throw ApiError(response.code(), response.message())
        eventDao.insert(EventEntity.fromDto(body))
    }

    override suspend fun upload(upload: MediaUpload): Media {
        val media = MultipartBody.Part.createFormData(
            "file",
            "name",
            upload.byteArray
                .toRequestBody("*/*".toMediaTypeOrNull())
        )
        val response = mediaService.uploadMedia(media)
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }

        return response.body() ?: throw ApiError(response.code(), response.message())
    }

    override suspend fun participate(id: Long) {
        eventDao.participate(id)
        val response = eventService.participate(id)
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        val body = response.body() ?: throw ApiError(response.code(), response.message())
        eventDao.insert(EventEntity.fromDto(body))
    }

    override suspend fun doNotParticipate(id: Long) {
        eventDao.doNotParticipate(id)
        val response = eventService.doNotParticipate(id)
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        val body = response.body() ?: throw ApiError(response.code(), response.message())
        eventDao.insert(EventEntity.fromDto(body))
    }
}