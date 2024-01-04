package com.example.nework.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nework.entity.EventEntity

@Dao
interface EventDao {
    @Query("SELECT * FROM EventEntity ORDER BY id DESC")
    fun pagingSource(): PagingSource<Int, EventEntity>

    @Query("SELECT * FROM EventEntity WHERE authorId = :authorId ORDER BY id DESC")
    fun pagingSource(authorId: Long): PagingSource<Int, EventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: EventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(events: List<EventEntity>)

    @Query("UPDATE EventEntity SET content = :content, datetime = :datetime WHERE id = :id")
    suspend fun updateContentById(id: Long, content: String, datetime: String)

    suspend fun save(eventEntity: EventEntity) =
        if (eventEntity.id == 0L)
            insert(eventEntity)
        else
            updateContentById(eventEntity.id, eventEntity.content, eventEntity.datetime)

    @Query("DELETE FROM EventEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("DELETE FROM EventEntity")
    suspend fun removeAll()

    @Query("UPDATE EventEntity SET likedByMe = 1 WHERE id = :id AND likedByMe = 0")
    suspend fun likeById(id: Long)

    @Query("UPDATE EventEntity SET likedByMe = 0 WHERE id = :id AND likedByMe = 1")
    suspend fun unlikeById(id: Long)

    @Query(
        "UPDATE EventEntity SET participatedByMe = 1 WHERE id = :id AND participatedByMe = 0"
    )
    suspend fun participate(id: Long)

    @Query(
        "UPDATE EventEntity SET participatedByMe = 0 WHERE id = :id AND participatedByMe = 1"
    )
    suspend fun doNotParticipate(id: Long)
}