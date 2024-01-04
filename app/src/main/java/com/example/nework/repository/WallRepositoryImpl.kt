package com.example.nework.repository


import com.example.nework.api.MediaService
import com.example.nework.api.PostService
import com.example.nework.api.WallService
import com.example.nework.dao.PostDao
import com.example.nework.dao.UserWallDao
import com.example.nework.dto.Attachment
import com.example.nework.dto.Media
import com.example.nework.dto.MediaUpload
import com.example.nework.dto.Post
import com.example.nework.entity.PostEntity
import com.example.nework.entity.UserPostEntity
import com.example.nework.entity.toDto
import com.example.nework.entity.toUserPostEntity
import com.example.nework.enums.AttachmentType
import com.example.nework.error.ApiError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WallRepositoryImpl @Inject constructor(
    private val dao: UserWallDao,
    private val service: WallService,
    private val mediaService: MediaService,
    private val postService: PostService,
    private val postDao: PostDao,
) :
    WallRepository {
    override val data: Flow<List<Post>> = dao.getAll()
        .map { it.toDto() }
        .flowOn(Dispatchers.Default)

    override suspend fun getUserWall(userId: Long) {
        dao.deleteAll()
        val response = service.getUserWall(userId)
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        val body = response.body() ?: throw ApiError(response.code(), response.message())
        dao.insert(body.toUserPostEntity())
    }

    override suspend fun removeById(id: Long) {
        val response = postService.removeById(id)
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        dao.removeById(id)
        postDao.removeById(id)
    }

    override suspend fun likeById(id: Long) {
        dao.likeById(id)
        val response = postService.likeById(id)
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        val body = response.body() ?: throw ApiError(response.code(), response.message())
        dao.insert(UserPostEntity.fromDto(body))
        postDao.update(PostEntity.fromDto(body))
    }

    override suspend fun unlikeById(id: Long) {
        dao.unlikeById(id)
        val response = postService.unlikeById(id)
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        val body = response.body() ?: throw ApiError(response.code(), response.message())
        dao.insert(UserPostEntity.fromDto(body))
        postDao.update(PostEntity.fromDto(body))
    }

    override suspend fun save(post: Post) {
        val response = postService.save(post)
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        val body = response.body() ?: throw ApiError(response.code(), response.message())
        dao.insert(UserPostEntity.fromDto(body))
        postDao.update(PostEntity.fromDto(body))
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
        dao.insert(UserPostEntity.fromDto(body))
        postDao.update(PostEntity.fromDto(body))
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