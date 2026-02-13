package com.example.movie_app.domain.usecase

import com.example.movie_app.domain.model.MovieDetails
import com.example.movie_app.domain.repository.MovieRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Use case for fetching movie details.
 * Implements Single Responsibility Principle - handles only movie details fetching logic.
 */
class GetMovieDetailsUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    /**
     * Executes the use case to fetch details for a specific movie.
     * @param movieId The ID of the movie.
     * @return Flow of Result containing movie details or an error.
     */
    operator fun invoke(movieId: Int): Flow<Result<MovieDetails>> {
        return repository.getMovieDetails(movieId)
    }
}

