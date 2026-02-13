package com.example.movie_app.data.repository

import app.cash.turbine.test
import com.example.movie_app.data.dto.MovieDto
import com.example.movie_app.data.dto.MoviesResponseDto
import com.example.movie_app.data.remote.TmdbApiService
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
            val result = awaitItem()
            assertTrue(result.isSuccess)
            val movies = result.getOrNull()!!
            assertEquals(1, movies.size)
            assertEquals("Test Movie", movies[0].title)
        }
    }

    @Test
    fun `getPopularMovies handles errors correctly`() = runTest {
        val error = Exception("Network error")
        coEvery { apiService.getPopularMovies(page = 1) } throws error

        repository.getPopularMovies(1).test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            assertEquals(error, result.exceptionOrNull())
        }
    }
}

