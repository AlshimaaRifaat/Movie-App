package com.example.movie_app.domain.usecase

import app.cash.turbine.test
import com.example.movie_app.domain.model.Movie
import com.example.movie_app.domain.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for GetPopularMoviesUseCase.
 */
class GetPopularMoviesUseCaseTest {

    private lateinit var repository: MovieRepository
    private lateinit var useCase: GetPopularMoviesUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetPopularMoviesUseCase(repository)
    }

    @Test
    fun `invoke returns movies from repository`() = runTest {
        val mockMovies = listOf(
            createMockMovie(1, "Movie 1"),
            createMockMovie(2, "Movie 2")
        )

        coEvery { repository.getPopularMovies(1) } returns flow {
            emit(Result.success(mockMovies))
        }

        useCase(1).test {
            val result = awaitItem()
            assertEquals(mockMovies, result.getOrNull())
        }
    }

    @Test
    fun `invoke with default page uses page 1`() = runTest {
        val mockMovies = emptyList<Movie>()

        coEvery { repository.getPopularMovies(1) } returns flow {
            emit(Result.success(mockMovies))
        }

        useCase().test {
            awaitItem()
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

