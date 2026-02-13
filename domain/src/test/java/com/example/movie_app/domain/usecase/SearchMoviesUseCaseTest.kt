package com.example.movie_app.domain.usecase

import app.cash.turbine.test
import com.example.movie_app.domain.model.Movie
import com.example.movie_app.domain.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for SearchMoviesUseCase.
 */
class SearchMoviesUseCaseTest {

    private lateinit var repository: MovieRepository
    private lateinit var useCase: SearchMoviesUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = SearchMoviesUseCase(repository)
    }

    @Test
    fun `invoke returns movies from repository`() = runTest {
        val mockMovies = listOf(
            createMockMovie(1, "Search Movie 1"),
            createMockMovie(2, "Search Movie 2")
        )

        coEvery { repository.searchMovies("test", 1) } returns flow {
            emit(Result.success(mockMovies))
        }

        useCase("test", 1).test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(mockMovies, result.getOrNull())
            awaitComplete()
        }
    }

    @Test
    fun `invoke with blank query returns empty list`() = runTest {
        // SearchMoviesUseCase should handle blank queries by returning empty list
        // without calling the repository
        useCase("", 1).test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            val movies = result.getOrNull()
            assertTrue(movies?.isEmpty() == true)
            awaitComplete()
        }
    }

    @Test
    fun `invoke with default page uses page 1`() = runTest {
        val mockMovies = emptyList<Movie>()

        coEvery { repository.searchMovies("test", 1) } returns flow {
            emit(Result.success(mockMovies))
        }

        useCase("test").test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            awaitComplete()
        }
    }

    @Test
    fun `invoke handles errors correctly`() = runTest {
        val error = Exception("Search failed")
        coEvery { repository.searchMovies("test", 1) } returns flow {
            emit(Result.failure(error))
        }

        useCase("test", 1).test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            assertEquals(error, result.exceptionOrNull())
            awaitComplete()
        }
    }

    private fun createMockMovie(id: Int, title: String): Movie {
        return Movie(
            id = id,
            title = title,
            overview = "Overview",
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg",
            releaseDate = "2024-01-01",
            voteAverage = 8.5,
            voteCount = 1000,
            popularity = 100.0
        )
    }
}
