package com.example.movie_app.domain.repository

import com.example.movie_app.domain.model.Movie
import com.example.movie_app.domain.model.MovieDetails
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for movie data operations.
 * This follows the Repository Pattern and Dependency Inversion Principle.
 */
interface MovieRepository {
    /**
     * Fetches a paginated list of popular movies.
     * @param page The page number to fetch (1-indexed).
     * @return Flow of Result containing a list of movies or an error.
     */
    fun getPopularMovies(page: Int): Flow<Result<List<Movie>>>

    /**
     * Fetches detailed information about a specific movie.
     * @param movieId The ID of the movie.
     * @return Flow of Result containing movie details or an error.
     */
    fun getMovieDetails(movieId: Int): Flow<Result<MovieDetails>>

    /**
     * Searches for movies by query string.
     * @param query The search query.
     * @param page The page number to fetch (1-indexed).
     * @return Flow of Result containing a list of matching movies or an error.
     */
    fun searchMovies(query: String, page: Int): Flow<Result<List<Movie>>>
}

