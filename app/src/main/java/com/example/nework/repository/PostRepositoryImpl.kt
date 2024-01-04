package com.example.nework.repository


import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.nework.api.MediaService
import com.example.nework.api.PostService
import com.example.nework.dao.PostDao
import com.example.nework.dao.PostRemoteKeyDao
import com.example.nework.db.AppDb
import com.example.nework.dto.Attachment
import com.example.nework.dto.Media
import com.example.nework.dto.MediaUpload
import com.example.nework.dto.Post
import com.example.nework.entity.PostEntity
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
class PostRepositoryImpl @Inject constructor(
    private val postService: PostService,
    private val mediaService: MediaService,
    db: AppDb,
    private val postDao: PostDao,
    postRemoteKeyDao: PostRemoteKeyDao,
) :
    PostRepository {
    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 5),
        remoteMediator = PostRemoteMediator(postService, db, postDao, postRemoteKeyDao),
        pagingSourceFactory = postDao::pagingSource,
    ).flow.map { pagingData ->
        pagingData.map(PostEntity::toDto)
    }

    override suspend fun removeById(id: Long) {
        val response = postService.removeById(id)
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        postDao.removeById(id)
    }

    override suspend fun likeById(id: Long) {
        postDao.likeById(id)
        val response = postService.likeById(id)
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        val body = response.body() ?: throw ApiError(response.code(), response.message())
        postDao.insert(PostEntity.fromDto(body))
    }

    override suspend fun unlikeById(id: Long) {
        postDao.unlikeById(id)
        val response = postService.unlikeById(id)
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        val body = response.body() ?: throw ApiError(response.code(), response.message())
        postDao.insert(PostEntity.fromDto(body))
    }

    override suspend fun save(post: Post) {
        val response = postService.save(post)
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        val body = response.body() ?: throw ApiError(response.code(), response.message())
        postDao.insert(PostEntity.fromDto(body))
    }

    override suspend fun saveWithAttachment(post: Post, upload: MediaUpload, type: AttachmentType) {
        val media = upload(upload)
        val postWithAttachment =
            post.copy(attachment = Attachment(media.url, type))
        val response = postService.save(postWithAttachment)
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        val body = response.body() ?: throw ApiError(response.code(), response.message())
        postDao.insert(PostEntity.fromDto(body))
    }

    private suspend fun upload(upload: MediaUpload): Media {
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
}