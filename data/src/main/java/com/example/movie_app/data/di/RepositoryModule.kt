package com.example.movie_app.data.di

import com.example.movie_app.data.repository.MovieRepositoryImpl
import com.example.movie_app.domain.repository.MovieRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

/**
 * Dagger Hilt module for providing repository implementations
 * Following Dependency Inversion Principle - binds implementation to interface
 */
@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class RepositoryModule {

    /**
     * Binds MovieRepositoryImpl to MovieRepository interface
     */
    @Binds
    @ActivityRetainedScoped
    abstract fun bindMovieRepository(
        movieRepositoryImpl: MovieRepositoryImpl
    ): MovieRepository
}

