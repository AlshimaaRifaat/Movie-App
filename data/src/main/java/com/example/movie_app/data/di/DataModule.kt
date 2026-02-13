package com.example.movie_app.data.di

import com.example.movie_app.data.remote.RetrofitModule
import com.example.movie_app.data.remote.TmdbApiService
import com.example.movie_app.data.repository.MovieRepositoryImpl
import com.example.movie_app.domain.repository.MovieRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Hilt module for Data layer dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    /**
     * Binds MovieRepositoryImpl to MovieRepository interface.
     * Follows Dependency Inversion Principle.
     */
    @Binds
    @Singleton
    abstract fun bindMovieRepository(
        movieRepositoryImpl: MovieRepositoryImpl
    ): MovieRepository
}

/**
 * Provides network-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return RetrofitModule.provideOkHttpClient()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return RetrofitModule.provideRetrofit(okHttpClient)
    }

    @Provides
    @Singleton
    fun provideTmdbApiService(retrofit: Retrofit): TmdbApiService {
        return RetrofitModule.provideTmdbApiService(retrofit)
    }
}

