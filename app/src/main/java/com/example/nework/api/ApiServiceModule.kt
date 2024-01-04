package com.example.nework.api

import com.example.nework.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.nework.auth.AppAuth
import retrofit2.create
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ApiServiceModule {

    companion object {
        private const val BASE_URL = "${BuildConfig.BASE_URL}/api/"
    }

    @Provides
    @Singleton
    fun providePostService(retrofit: Retrofit): PostService =
        retrofit.create()

    @Provides
    @Singleton
    fun provideMediaService(retrofit: Retrofit): MediaService =
        retrofit.create()


    @Provides
    @Singleton
    fun provideUserService(retrofit: Retrofit): UserService =
        retrofit.create()

    @Provides
    @Singleton
    fun provideJobService(retrofit: Retrofit): JobService =
        retrofit.create()

    @Provides
    @Singleton
    fun provideWallService(retrofit: Retrofit): WallService =
        retrofit.create()

    @Provides
    @Singleton
    fun provideEventService(retrofit: Retrofit): EventService =
        retrofit.create()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(client)
        .build()

    @Provides
    @Singleton
    fun provideOkhttp(auth: AppAuth): OkHttpClient =
        okhttp(authInterceptor(auth), loggingInterceptor())


    private fun okhttp(vararg interceptors: Interceptor): OkHttpClient = OkHttpClient.Builder()
        .apply {
            interceptors.forEach {
                this.addInterceptor(it)
            }
        }
        .build()

    private fun authInterceptor(auth: AppAuth) = fun(chain: Interceptor.Chain): Response {
        auth.authStateFlow.value.token?.let { token ->
            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", token)
                .build()
            return chain.proceed(newRequest)
        }

        return chain.proceed(chain.request())
    }

    private fun loggingInterceptor() = HttpLoggingInterceptor()
        .apply {
            if (BuildConfig.DEBUG) {
                level = HttpLoggingInterceptor.Level.BODY
            }
        }
}