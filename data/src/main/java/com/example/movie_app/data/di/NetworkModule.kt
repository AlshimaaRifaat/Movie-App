package com.example.movie_app.data.di

import com.example.movie_app.data.remote.RetrofitModule
import com.example.movie_app.data.remote.TmdbApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing network dependencies
 * Following Dependency Injection best practices
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Provides OkHttpClient with logging interceptor
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return RetrofitModule.provideOkHttpClient()
    }

    /**
     * Provides Retrofit instance
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return RetrofitModule.provideRetrofit(okHttpClient)
    }

    /**
     * Provides TmdbApiService
     */
    @Provides
    @Singleton
    fun provideTmdbApiService(retrofit: Retrofit): TmdbApiService {
        return RetrofitModule.provideTmdbApiService(retrofit)
    }
}

