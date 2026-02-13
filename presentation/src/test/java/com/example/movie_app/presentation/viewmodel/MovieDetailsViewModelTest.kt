package com.example.movie_app.presentation.viewmodel

import app.cash.turbine.test
import com.example.movie_app.domain.model.Genre
import com.example.movie_app.domain.model.MovieDetails
import com.example.movie_app.domain.model.ProductionCompany
import com.example.movie_app.domain.usecase.GetMovieDetailsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for MovieDetailsViewModel.
 */
class MovieDetailsViewModelTest {

    private lateinit var getMovieDetailsUseCase: GetMovieDetailsUseCase
    private lateinit var viewModel: MovieDetailsViewModel

    @Before
    fun setup() {
        getMovieDetailsUseCase = mockk()
        viewModel = MovieDetailsViewModel(getMovieDetailsUseCase)
    }

    @Test
    fun `initial state is correct`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(null, state.movieDetails)
            assertFalse(state.isLoading)
            assertEquals(null, state.error)
        }
    }

    @Test
    fun `loadMovieDetails success updates state correctly`() = runTest {
        val mockDetails = createMockMovieDetails(1, "Test Movie")

        coEvery { getMovieDetailsUseCase(1) } returns flow {
            emit(Result.success(mockDetails))
        }

        viewModel.loadMovieDetails(1)

        viewModel.uiState.test {
            skipItems(1) // Skip initial state
            val state = awaitItem()
            assertNotNull(state.movieDetails)
            assertEquals("Test Movie", state.movieDetails?.title)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `loadMovieDetails failure updates error state`() = runTest {
        val errorMessage = "Movie not found"
        coEvery { getMovieDetailsUseCase(1) } returns flow {
            emit(Result.failure(Exception(errorMessage)))
        }

        viewModel.loadMovieDetails(1)

        viewModel.uiState.test {
            skipItems(1) // Skip initial state
            val state = awaitItem()
            assertEquals(errorMessage, state.error)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `retry reloads movie details`() = runTest {
        val mockDetails = createMockMovieDetails(1, "Test Movie")

        coEvery { getMovieDetailsUseCase(1) } returns flow {
            emit(Result.success(mockDetails))
        }

        viewModel.retry(1)

        viewModel.uiState.test {
            skipItems(1) // Skip initial state
            val state = awaitItem()
            assertNotNull(state.movieDetails)
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

