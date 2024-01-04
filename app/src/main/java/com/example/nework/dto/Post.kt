package com.example.nework.dto

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Post(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String? = null,
    val authorJob: String? = null,
    val content: String,
    val published: String,
    val coords: Coordinates? = null,
    val link: String? = null,
    val likeOwnerIds: Set<Long> = emptySet(),
    val mentionIds: Set<Long> = emptySet(),
    val mentionedMe: Boolean = false,
    val likedByMe: Boolean = false,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
//    val users: List<UserPreview> = emptyList(),
)

fun Post.publishedFormatted(): String {
        val now = LocalDateTime.now()
        val yesterdayDateTime = now.minusDays(1)
        val datetime = LocalDateTime.parse(published, DateTimeFormatter.ISO_DATE_TIME)
        return when {
            datetime.year == now.year && datetime.dayOfYear == now.dayOfYear ->
                "today at ${datetime.format(DateTimeFormatter.ofPattern("HH:MM"))}"

            datetime.year == yesterdayDateTime.year && datetime.dayOfYear == yesterdayDateTime.dayOfYear ->
                "yesterday at ${datetime.format(DateTimeFormatter.ofPattern("HH:mm"))}"

            datetime.year < yesterdayDateTime.year || (datetime.year <= yesterdayDateTime.year && datetime.dayOfYear < yesterdayDateTime.dayOfYear) ->
                "${datetime.format(DateTimeFormatter.ofPattern("dd.MM"))} at ${
                    datetime.format(
                        DateTimeFormatter.ofPattern("HH:mm")
                    )
                }"

            else -> published
        }
    }
