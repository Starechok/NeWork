package com.example.nework.dao

import com.example.nework.db.AppDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@InstallIn(SingletonComponent::class)
@Module
object DaoModule {
    @Provides
    fun providePostDao(db: AppDb): PostDao = db.postDao()

    @Provides
    fun providePostRemoteKeyDao(db: AppDb): PostRemoteKeyDao = db.postRemoteKeyDao()

    @Provides
    fun provideUserDao(db: AppDb): UserDao = db.userDao()

    @Provides
    fun provideJobDao(db: AppDb): JobDao = db.jobDao()

    @Provides
    fun provideUserWallDao(db: AppDb): UserWallDao = db.userWallDao()

    @Provides
    fun provideEventDao(db: AppDb): EventDao = db.eventDao()

    @Provides
    fun provideEvenRemoteKeyDao(db: AppDb): EventRemoteKeyDao = db.eventRemoteKeyDao()
}