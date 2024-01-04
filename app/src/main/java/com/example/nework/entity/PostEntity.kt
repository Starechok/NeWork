package com.example.nework.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nework.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val authorJob: String?,
    val content: String,
    val published: String,
    @Embedded
    val coordinates: CoordinatesEmbeddable?,
    val link: String? = null,
    val likeOwnerIds: Set<Long> = emptySet(),
    val mentionIds: Set<Long> = emptySet(),
    val mentionedMe: Boolean,
    val likedByMe: Boolean,
    @Embedded
    val attachment: AttachmentEmbeddable? = null,
    val ownedByMe: Boolean,
//    val users: List<UserPreview> = emptyList(),
) {
    fun toDto() = Post(
        id,
        authorId,
        author,
        authorAvatar,
        authorJob,
        content,
        published,
        coordinates?.toDto(),
        link,
        likeOwnerIds,
        mentionIds,
        mentionedMe,
        likedByMe,
        attachment?.toDto(),
        ownedByMe,
//        users,
    )

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                dto.id,
                dto.authorId,
                dto.author,
                dto.authorAvatar,
                dto.authorJob,
                dto.content,
                dto.published,
                CoordinatesEmbeddable.fromDto(dto.coords),
                dto.link,
                dto.likeOwnerIds,
                dto.mentionIds,
                dto.mentionedMe,
                dto.likedByMe,
                AttachmentEmbeddable.fromDto(dto.attachment),
                dto.likedByMe,
//                dto.users,
            )
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)
