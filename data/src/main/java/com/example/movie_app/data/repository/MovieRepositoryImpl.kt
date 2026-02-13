package com.example.movie_app.data.repository

import com.example.movie_app.data.remote.TmdbApiService
import com.example.movie_app.domain.model.Movie
import com.example.movie_app.domain.model.MovieDetails
import com.example.movie_app.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Implementation of MovieRepository.
 * Handles data fetching from remote API and maps DTOs to domain models.
 */
class MovieRepositoryImpl @Inject constructor(
    private val apiService: TmdbApiService
) : MovieRepository {

    override fun getPopularMovies(page: Int): Flow<Result<List<Movie>>> = flow {
        try {
            val response = apiService.getPopularMovies(page = page)
            val movies = response.results.map { it.toDomain() }
            emit(Result.success(movies))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getMovieDetails(movieId: Int): Flow<Result<MovieDetails>> = flow {
        try {
            val response = apiService.getMovieDetails(movieId)
            emit(Result.success(response.toDomain()))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun searchMovies(query: String, page: Int): Flow<Result<List<Movie>>> = flow {
        try {
            val response = apiService.searchMovies(query = query, page = page)
            val movies = response.results.map { it.toDomain() }
            emit(Result.success(movies))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}

