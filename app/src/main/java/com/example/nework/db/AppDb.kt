package com.example.nework.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.nework.dao.EventDao
import com.example.nework.dao.EventRemoteKeyDao
import com.example.nework.dao.JobDao
import com.example.nework.dao.PostDao
import com.example.nework.dao.PostRemoteKeyDao
import com.example.nework.dao.UserDao
import com.example.nework.dao.UserWallDao
import com.example.nework.entity.EventEntity
import com.example.nework.entity.EventRemoteKeyEntity
import com.example.nework.entity.JobEntity
import com.example.nework.entity.PostEntity
import com.example.nework.entity.PostRemoteKeyEntity
import com.example.nework.entity.UserEntity
import com.example.nework.entity.UserPostEntity
import com.example.nework.utils.Converters


@Database(
    entities = [PostEntity::class, PostRemoteKeyEntity::class, UserEntity::class, JobEntity::class,
        UserPostEntity::class, EventEntity::class, EventRemoteKeyEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
    abstract fun userDao(): UserDao
    abstract fun jobDao(): JobDao
    abstract fun userWallDao(): UserWallDao
    abstract fun eventDao(): EventDao
    abstract fun eventRemoteKeyDao(): EventRemoteKeyDao

}