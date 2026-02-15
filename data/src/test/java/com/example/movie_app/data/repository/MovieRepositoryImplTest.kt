package com.example.movie_app.data.repository

import app.cash.turbine.test
import com.example.movie_app.data.dto.MovieDto
import com.example.movie_app.data.dto.MoviesResponseDto
import com.example.movie_app.data.remote.TmdbApiService
import com.example.movie_app.domain.model.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for MovieRepositoryImpl.
 */
class MovieRepositoryImplTest {

    private lateinit var apiService: TmdbApiService
    private lateinit var repository: MovieRepositoryImpl

    @Before
    fun setup() {
        apiService = mockk()
        repository = MovieRepositoryImpl(apiService)
    }

    @Test
    fun `getPopularMovies maps DTOs to domain models`() = runTest {
        val mockResponse = MoviesResponseDto(
            page = 1,
            results = listOf(
                MovieDto(
                    id = 1,
                    title = "Test Movie",
                    overview = "Overview",
                    posterPath = "/poster.jpg",
                    backdropPath = "/backdrop.jpg",
                    releaseDate = "2024-01-01",
                    voteAverage = 8.5,
                    voteCount = 1000,
                    popularity = 100.0
                )
            ),
            totalPages = 10,
            totalResults = 100
        )

        coEvery { apiService.getPopularMovies(page = 1) } returns mockResponse

        repository.getPopularMovies(1).test {
            // First item should be Loading
            val loadingResult = awaitItem()
            assertTrue(loadingResult.isLoading)
            
            // Second item should be Success
            val result = awaitItem()
            assertTrue(result.isSuccess)
            val movies = result.getDataOrNull()!!
            assertEquals(1, movies.size)
            assertEquals("Test Movie", movies[0].title)
            awaitComplete()
        }
    }

    @Test
    fun `getPopularMovies handles errors correctly`() = runTest {
        val error = Exception("Network error")
        coEvery { apiService.getPopularMovies(page = 1) } throws error

        repository.getPopularMovies(1).test {
            // First item should be Loading
            val loadingResult = awaitItem()
            assertTrue(loadingResult.isLoading)
            
            // Second item should be Error
            val result = awaitItem()
            assertTrue(result.isError)
            assertTrue(result is Result.Error)
            val errorResult = result as Result.Error
            assertEquals("Network error", errorResult.exception.message)
            awaitComplete()
        }
    }
    
    @Test
    fun `getMovieDetails maps DTO to domain model`() = runTest {
        val mockDetails = com.example.movie_app.data.dto.MovieDetailsDto(
            id = 1,
            title = "Test Movie",
            overview = "Overview",
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg",
            releaseDate = "2024-01-01",
            voteAverage = 8.5,
            voteCount = 1000,
            popularity = 100.0,
            runtime = 120,
            genres = listOf(com.example.movie_app.data.dto.GenreDto(1, "Action")),
            productionCompanies = listOf(com.example.movie_app.data.dto.ProductionCompanyDto(1, "Studio", "/logo.jpg")),
            tagline = "Tagline"
        )

        coEvery { apiService.getMovieDetails(1) } returns mockDetails

        repository.getMovieDetails(1).test {
            // First item should be Loading
            val loadingResult = awaitItem()
            assertTrue(loadingResult.isLoading)
            
            // Second item should be Success
            val result = awaitItem()
            assertTrue(result.isSuccess)
            val details = result.getDataOrNull()!!
            assertEquals("Test Movie", details.title)
            assertEquals(120, details.runtime)
            assertEquals(1, details.genres.size)
            awaitComplete()
        }
    }
    
    @Test
    fun `searchMovies maps DTOs to domain models`() = runTest {
        val mockResponse = MoviesResponseDto(
            page = 1,
            results = listOf(
                MovieDto(
                    id = 1,
                    title = "Search Movie",
                    overview = "Overview",
                    posterPath = "/poster.jpg",
                    backdropPath = "/backdrop.jpg",
                    releaseDate = "2024-01-01",
                    voteAverage = 8.5,
                    voteCount = 1000,
                    popularity = 100.0
                )
            ),
            totalPages = 10,
            totalResults = 100
        )

        coEvery { apiService.searchMovies(query = "test", page = 1) } returns mockResponse

        repository.searchMovies("test", 1).test {
            // First item should be Loading
            val loadingResult = awaitItem()
            assertTrue(loadingResult.isLoading)
            
            // Second item should be Success
            val result = awaitItem()
            assertTrue(result.isSuccess)
            val movies = result.getDataOrNull()!!
            assertEquals(1, movies.size)
            assertEquals("Search Movie", movies[0].title)
            awaitComplete()
        }
    }
}
