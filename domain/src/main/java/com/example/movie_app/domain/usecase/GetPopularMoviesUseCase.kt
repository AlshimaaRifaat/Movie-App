package com.example.movie_app.domain.usecase

import com.example.movie_app.domain.model.Movie
import com.example.movie_app.domain.repository.MovieRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Use case for fetching popular movies.
 * Implements Single Responsibility Principle - handles only movie fetching logic.
 */
class GetPopularMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    /**
     * Executes the use case to fetch popular movies for a given page.
     * @param page The page number to fetch (defaults to 1).
     * @return Flow of Result containing a list of movies or an error.
     */
    operator fun invoke(page: Int = 1): Flow<Result<List<Movie>>> {
        return repository.getPopularMovies(page)
    }
}

