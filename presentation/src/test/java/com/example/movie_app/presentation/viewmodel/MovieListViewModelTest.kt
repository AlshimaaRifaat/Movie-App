package com.example.movie_app.presentation.viewmodel

import app.cash.turbine.test
import com.example.movie_app.domain.model.Movie
import com.example.movie_app.domain.usecase.GetPopularMoviesUseCase
import com.example.movie_app.domain.usecase.SearchMoviesUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for MovieListViewModel.
 */
class MovieListViewModelTest {

    private lateinit var getPopularMoviesUseCase: GetPopularMoviesUseCase
    private lateinit var searchMoviesUseCase: SearchMoviesUseCase
    private lateinit var viewModel: MovieListViewModel

    @Before
    fun setup() {
        getPopularMoviesUseCase = mockk()
        searchMoviesUseCase = mockk()
        viewModel = MovieListViewModel(getPopularMoviesUseCase, searchMoviesUseCase)
    }

    @Test
    fun `initial state is correct`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.movies.isEmpty())
            assertFalse(state.isLoading)
            assertEquals(null, state.error)
            assertEquals(1, state.currentPage)
            assertTrue(state.hasMore)
        }
    }

    @Test
    fun `loadMovies success updates state correctly`() = runTest {
        val mockMovies = listOf(
            createMockMovie(1, "Movie 1"),
            createMockMovie(2, "Movie 2")
        )

        coEvery { getPopularMoviesUseCase(1) } returns flow {
            emit(Result.success(mockMovies))
        }

        viewModel.loadMovies()

        viewModel.uiState.test {
            skipItems(1) // Skip initial state
            val state = awaitItem()
            assertEquals(2, state.movies.size)
            assertFalse(state.isLoading)
            assertEquals(2, state.currentPage)
        }
    }

    @Test
    fun `loadMovies failure updates error state`() = runTest {
        val errorMessage = "Network error"
        coEvery { getPopularMoviesUseCase(1) } returns flow {
            emit(Result.failure(Exception(errorMessage)))
        }

        viewModel.loadMovies()

        viewModel.uiState.test {
            skipItems(1) // Skip initial state
            val state = awaitItem()
            assertEquals(errorMessage, state.error)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `searchMovies updates search query and results`() = runTest {
        val query = "test"
        val mockMovies = listOf(createMockMovie(1, "Test Movie"))

        coEvery { searchMoviesUseCase(query, 1) } returns flow {
            emit(Result.success(mockMovies))
        }

        viewModel.searchMovies(query)

        viewModel.uiState.test {
            skipItems(1) // Skip initial state
            val state = awaitItem()
            assertEquals(query, state.searchQuery)
            assertTrue(state.isSearchMode)
            assertEquals(1, state.movies.size)
        }
    }

    @Test
    fun `clearError removes error from state`() = runTest {
        val errorMessage = "Error"
        coEvery { getPopularMoviesUseCase(1) } returns flow {
            emit(Result.failure(Exception(errorMessage)))
        }

        viewModel.loadMovies()
        viewModel.clearError()

        viewModel.uiState.test {
            skipItems(2) // Skip initial and error state
            val state = awaitItem()
            assertEquals(null, state.error)
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

