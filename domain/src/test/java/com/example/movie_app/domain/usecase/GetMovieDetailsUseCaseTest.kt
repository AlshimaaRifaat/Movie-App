package com.example.movie_app.domain.usecase

import app.cash.turbine.test
import com.example.movie_app.domain.model.Genre
import com.example.movie_app.domain.model.MovieDetails
import com.example.movie_app.domain.model.ProductionCompany
import com.example.movie_app.domain.model.Result
import com.example.movie_app.domain.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for GetMovieDetailsUseCase.
 */
class GetMovieDetailsUseCaseTest {

    private lateinit var repository: MovieRepository
    private lateinit var useCase: GetMovieDetailsUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetMovieDetailsUseCase(repository)
    }

    @Test
    fun `invoke returns movie details from repository`() = runTest {
        val mockDetails = createMockMovieDetails(1, "Test Movie")

        coEvery { repository.getMovieDetails(1) } returns flow {
            emit(Result.Loading)
            emit(Result.Success(mockDetails))
        }

        useCase(1).test {
            // First item should be Loading
            val loadingResult = awaitItem()
            assertTrue(loadingResult.isLoading)
            
            // Second item should be Success
            val result = awaitItem()
            assertTrue(result.isSuccess)
            val details = result.getDataOrNull()
            assertNotNull(details)
            assertEquals("Test Movie", details?.title)
            assertEquals(1, details?.id)
            awaitComplete()
        }
    }

    @Test
    fun `invoke handles errors correctly`() = runTest {
        val error = Exception("Movie not found")
        coEvery { repository.getMovieDetails(1) } returns flow {
            emit(Result.Loading)
            emit(Result.Error(error, error.message))
        }

        useCase(1).test {
            // First item should be Loading
            val loadingResult = awaitItem()
            assertTrue(loadingResult.isLoading)
            
            // Second item should be Error
            val result = awaitItem()
            assertTrue(result.isError)
            when (val errorResult = result) {
                is Result.Error -> assertEquals(error, errorResult.exception)
                else -> throw AssertionError("Expected Result.Error")
            }
            awaitComplete()
        }
    }

    private fun createMockMovieDetails(id: Int, title: String): MovieDetails {
        return MovieDetails(
            id = id,
            title = title,
            overview = "Overview",
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg",
            releaseDate = "2024-01-01",
            voteAverage = 8.5,
            voteCount = 1000,
            popularity = 100.0,
            runtime = 120,
            genres = listOf(Genre(1, "Action")),
            productionCompanies = listOf(ProductionCompany(1, "Studio", "/logo.jpg")),
            tagline = "Tagline"
        )
    }
}
