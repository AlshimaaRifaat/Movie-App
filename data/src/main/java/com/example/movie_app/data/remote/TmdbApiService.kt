package com.example.movie_app.data.remote

import com.example.movie_app.data.BuildConfig
import com.example.movie_app.data.dto.MovieDetailsDto
import com.example.movie_app.data.dto.MoviesResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API service interface for The Movie Database (TMDB) API.
 */
interface TmdbApiService {
    /**
     * Fetches popular movies with pagination.
     * @param apiKey The TMDB API key.
     * @param page The page number (defaults to 1).
     * @return Response containing paginated movie list.
     */
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY,
        @Query("page") page: Int = 1
    ): MoviesResponseDto

    /**
     * Fetches detailed information about a specific movie.
     * @param movieId The ID of the movie.
     * @param apiKey The TMDB API key.
     * @return Movie details response.
     */
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): MovieDetailsDto

    /**
     * Searches for movies by query string.
     * @param apiKey The TMDB API key.
     * @param query The search query.
     * @param page The page number (defaults to 1).
     * @return Response containing paginated search results.
     */
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY,
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): MoviesResponseDto
}

