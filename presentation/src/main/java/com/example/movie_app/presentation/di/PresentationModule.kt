package com.example.movie_app.presentation.di

import com.example.movie_app.domain.usecase.GetMovieDetailsUseCase
import com.example.movie_app.domain.usecase.GetPopularMoviesUseCase
import com.example.movie_app.domain.usecase.SearchMoviesUseCase
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

/**
 * Hilt module for Presentation layer dependencies.
 * Provides use cases to ViewModels.
 */
@Module
@InstallIn(ViewModelComponent::class)
object PresentationModule {
    // Use cases are automatically provided by Hilt through constructor injection
    // No explicit bindings needed as they are concrete classes
}

