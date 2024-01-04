package com.example.nework.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.nework.entity.PostEntity


@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun pagingSource(): PagingSource<Int, PostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("DELETE FROM PostEntity")
    suspend fun removeAll()

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("UPDATE PostEntity SET likedByMe = 1 WHERE id = :id AND likedByMe = 0")
    suspend fun likeById(id: Long)

    @Query("UPDATE PostEntity SET likedByMe = 0 WHERE id = :id AND likedByMe = 1")
    suspend fun unlikeById(id: Long)

    @Update(PostEntity::class)
    suspend fun update(post: PostEntity)
}