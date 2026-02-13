package com.example.movie_app.domain.usecase

import com.example.movie_app.domain.model.Movie
import com.example.movie_app.domain.repository.MovieRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Use case for searching movies.
 * Implements Single Responsibility Principle - handles only movie search logic.
 */
class SearchMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    /**
     * Executes the use case to search for movies.
     * @param query The search query string.
     * @param page The page number to fetch (defaults to 1).
     * @return Flow of Result containing a list of matching movies or an error.
     */
    operator fun invoke(query: String, page: Int = 1): Flow<Result<List<Movie>>> {
        return if (query.isBlank()) {
            kotlinx.coroutines.flow.flow { emit(Result.success(emptyList())) }
        } else {
            repository.searchMovies(query, page)
        }
    }
}

