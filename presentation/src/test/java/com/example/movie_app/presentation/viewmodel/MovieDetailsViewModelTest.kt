package com.example.movie_app.presentation.viewmodel

import com.example.movie_app.domain.model.Genre
import com.example.movie_app.domain.model.MovieDetails
import com.example.movie_app.domain.model.ProductionCompany
import com.example.movie_app.domain.model.Result
import com.example.movie_app.domain.usecase.GetMovieDetailsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
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
        Dispatchers.setMain(kotlinx.coroutines.test.UnconfinedTestDispatcher())
        getMovieDetailsUseCase = mockk()
        viewModel = MovieDetailsViewModel(getMovieDetailsUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        // Get the current state value directly
        val state = viewModel.uiState.value
        assertEquals(null, state.movieDetails)
        assertFalse(state.isLoading)
        assertEquals(null, state.error)
    }

    @Test
    fun `loadMovieDetails success updates state correctly`() = runTest {
        val mockDetails = createMockMovieDetails(1, "Test Movie")

        coEvery { getMovieDetailsUseCase(1) } returns flow {
            emit(Result.Loading)
            emit(Result.Success(mockDetails))
        }

        viewModel.loadMovieDetails(1)
        advanceUntilIdle()

        // Get the current state value directly
        val state = viewModel.uiState.value
        assertNotNull(state.movieDetails)
        assertEquals("Test Movie", state.movieDetails?.title)
        assertFalse(state.isLoading)
        assertEquals(null, state.error)
    }

    @Test
    fun `loadMovieDetails failure updates error state`() = runTest {
        val error = java.net.UnknownHostException("Movie not found")
        coEvery { getMovieDetailsUseCase(1) } returns flow {
            emit(Result.Loading)
            emit(Result.Error(error, error.message))
        }

        viewModel.loadMovieDetails(1)
        advanceUntilIdle()

        // Get the current state value directly
        val state = viewModel.uiState.value
        // ErrorHandler converts UnknownHostException to user-friendly message
        assertTrue(state.error != null)
        assertTrue(
            state.error?.contains("No internet connection") == true ||
            state.error?.contains("Movie not found") == true ||
            state.error?.contains("Network") == true
        )
        assertFalse(state.isLoading)
    }

    @Test
    fun `retry reloads movie details`() = runTest {
        val mockDetails = createMockMovieDetails(1, "Test Movie")

        coEvery { getMovieDetailsUseCase(1) } returns flow {
            emit(Result.Loading)
            emit(Result.Success(mockDetails))
        }

        viewModel.retry(1)
        advanceUntilIdle()

        // Get the current state value directly
        val state = viewModel.uiState.value
        assertNotNull(state.movieDetails)
        assertEquals("Test Movie", state.movieDetails?.title)
        assertFalse(state.isLoading)
        assertEquals(null, state.error)
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

