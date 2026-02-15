package com.example.movie_app.presentation.viewmodel

import com.example.movie_app.domain.model.Movie
import com.example.movie_app.domain.model.Result
import com.example.movie_app.domain.usecase.GetPopularMoviesUseCase
import com.example.movie_app.domain.usecase.SearchMoviesUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.After
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
        Dispatchers.setMain(UnconfinedTestDispatcher())
        getPopularMoviesUseCase = mockk(relaxed = true)
        searchMoviesUseCase = mockk(relaxed = true)
        // Mock the initial loadMovies() call in init block to return a movie
        // so that hasMore is true and we can test pagination.
        coEvery { getPopularMoviesUseCase(1) } returns flow {
            emit(Result.Loading)
            emit(Result.Success(listOf(createMockMovie(0, "Initial Movie"))))
        }
        viewModel = MovieListViewModel(getPopularMoviesUseCase, searchMoviesUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        advanceUntilIdle() // Wait for init block to complete
        val state = viewModel.uiState.value
        assertEquals(1, state.movies.size)
        assertFalse(state.isLoading)
        assertEquals(null, state.error)
        assertEquals(2, state.currentPage)
        assertTrue(state.hasMore)
    }

    @Test
    fun `loadMovies success appends movies to state`() = runTest {
        advanceUntilIdle() // Init loads page 1

        val mockMovies = listOf(
            createMockMovie(1, "Movie 1"),
            createMockMovie(2, "Movie 2")
        )
        // Mock for page 2
        coEvery { getPopularMoviesUseCase(2) } returns flow {
            emit(Result.Loading)
            emit(Result.Success(mockMovies))
        }

        viewModel.loadMovies()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(3, state.movies.size) // 1 initial + 2 new
        assertFalse(state.isLoading)
        assertTrue(state.hasMore)
        assertEquals(3, state.currentPage)
        assertEquals(null, state.error)
    }

    @Test
    fun `loadMovies failure updates error state`() = runTest {
        advanceUntilIdle() // Init loads page 1

        val error = java.net.UnknownHostException("Network error")
        // Mock for page 2 to fail
        coEvery { getPopularMoviesUseCase(2) } returns flow {
            emit(Result.Loading)
            emit(Result.Error(error, error.message))
        }

        viewModel.loadMovies()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.error != null)
        assertTrue(state.error!!.contains("No internet connection"))
        assertFalse(state.isLoading)
        assertEquals(1, state.movies.size) // list is unchanged
        assertEquals(2, state.currentPage) // page does not advance
    }

    @Test
    fun `searchMovies updates search query and results`() = runTest {
        advanceUntilIdle()
        val query = "test"
        val mockMovies = listOf(createMockMovie(1, "Test Movie"))

        coEvery { searchMoviesUseCase(query, 1) } returns flow {
            emit(Result.Loading)
            emit(Result.Success(mockMovies))
        }

        viewModel.searchMovies(query)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(query, state.searchQuery)
        assertTrue(state.isSearchMode)
        assertEquals(1, state.movies.size)
        assertFalse(state.isLoading)
        assertEquals(null, state.error)
    }

    @Test
    fun `clearError removes error from state`() = runTest {
        advanceUntilIdle() // Init loads page 1

        val error = java.io.IOException("Some error")
        coEvery { getPopularMoviesUseCase(2) } returns flow {
            emit(Result.Loading)
            emit(Result.Error(error, error.message))
        }
        
        viewModel.loadMovies()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.error != null)

        viewModel.clearError()

        assertEquals(null, viewModel.uiState.value.error)
    }
    
    @Test
    fun `retry with popular movies calls loadMovies`() = runTest {
        advanceUntilIdle()
        // initial state: not search mode

        val mockMovies = listOf(createMockMovie(1, "Movie 1"))
        coEvery { getPopularMoviesUseCase(2) } returns flow {
            emit(Result.Loading)
            emit(Result.Success(mockMovies))
        }

        viewModel.retry()
        advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.movies.size)
        assertEquals(3, viewModel.uiState.value.currentPage)
    }
    
    @Test
    fun `retry with search calls searchMovies`() = runTest {
        advanceUntilIdle()
        val query = "test"
        val searchResults = listOf(createMockMovie(10, "Search Result"))
        coEvery { searchMoviesUseCase(query, 1) } returns flow {
            emit(Result.Loading)
            emit(Result.Success(searchResults))
        }

        // Enter search mode
        viewModel.searchMovies(query)
        advanceUntilIdle()
        
        assertEquals(query, viewModel.uiState.value.searchQuery)
        assertEquals(1, viewModel.uiState.value.movies.size)

        // Mock retry call
        val moreSearchResults = listOf(createMockMovie(11, "More Search Results"))
        coEvery { searchMoviesUseCase(query, 1) } returns flow {
            emit(Result.Loading)
            emit(Result.Success(moreSearchResults))
        }

        viewModel.retry()
        advanceUntilIdle()
        
        // Retry in search mode re-runs the search, replacing movies
        assertEquals(1, viewModel.uiState.value.movies.size) 
        assertEquals("More Search Results", viewModel.uiState.value.movies.first().title)
    }

    @Test
    fun `getAutocompleteSuggestions shows suggestions for valid query`() = runTest {
        advanceUntilIdle()
        val query = "batman"
        val suggestions = listOf(
            createMockMovie(1, "Batman"),
            createMockMovie(2, "Batman Begins"),
            createMockMovie(3, "The Dark Knight")
        )

        coEvery { searchMoviesUseCase(query, 1) } returns flow {
            emit(Result.Loading)
            emit(Result.Success(suggestions))
        }

        viewModel.getAutocompleteSuggestions(query)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(3, state.autocompleteSuggestions.size)
        assertTrue(state.showAutocomplete)
    }

    @Test
    fun `getAutocompleteSuggestions limits to 5 suggestions`() = runTest {
        advanceUntilIdle()
        val query = "test"
        val manySuggestions = (1..10).map { createMockMovie(it, "Test Movie $it") }

        coEvery { searchMoviesUseCase(query, 1) } returns flow {
            emit(Result.Loading)
            emit(Result.Success(manySuggestions))
        }

        viewModel.getAutocompleteSuggestions(query)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(5, state.autocompleteSuggestions.size)
        assertTrue(state.showAutocomplete)
    }

    @Test
    fun `getAutocompleteSuggestions hides autocomplete for blank query`() = runTest {
        advanceUntilIdle()
        
        viewModel.getAutocompleteSuggestions("")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.autocompleteSuggestions.isEmpty())
        assertFalse(state.showAutocomplete)
    }

    @Test
    fun `getAutocompleteSuggestions hides autocomplete for short query`() = runTest {
        advanceUntilIdle()
        
        viewModel.getAutocompleteSuggestions("a")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.autocompleteSuggestions.isEmpty())
        assertFalse(state.showAutocomplete)
    }

    @Test
    fun `getAutocompleteSuggestions handles errors gracefully`() = runTest {
        advanceUntilIdle()
        val query = "test"
        val error = Exception("Network error")

        coEvery { searchMoviesUseCase(query, 1) } returns flow {
            emit(Result.Loading)
            emit(Result.Error(error, error.message))
        }

        viewModel.getAutocompleteSuggestions(query)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.autocompleteSuggestions.isEmpty())
        assertFalse(state.showAutocomplete)
    }

    @Test
    fun `hideAutocomplete clears suggestions and hides dropdown`() = runTest {
        advanceUntilIdle()
        val query = "test"
        val suggestions = listOf(createMockMovie(1, "Test Movie"))

        coEvery { searchMoviesUseCase(query, 1) } returns flow {
            emit(Result.Loading)
            emit(Result.Success(suggestions))
        }

        viewModel.getAutocompleteSuggestions(query)
        advanceUntilIdle()

        var state = viewModel.uiState.value
        assertTrue(state.showAutocomplete)
        assertEquals(1, state.autocompleteSuggestions.size)

        viewModel.hideAutocomplete()

        state = viewModel.uiState.value
        assertFalse(state.showAutocomplete)
        assertTrue(state.autocompleteSuggestions.isEmpty())
    }

    @Test
    fun `resetSearch clears search and returns to popular movies`() = runTest {
        advanceUntilIdle()
        val query = "test"
        val searchResults = listOf(createMockMovie(1, "Test Movie"))

        // Set up search mode
        coEvery { searchMoviesUseCase(query, 1) } returns flow {
            emit(Result.Loading)
            emit(Result.Success(searchResults))
        }
        viewModel.searchMovies(query)
        advanceUntilIdle()

        var state = viewModel.uiState.value
        assertTrue(state.isSearchMode)
        assertEquals(query, state.searchQuery)

        // Mock popular movies for reset
        coEvery { getPopularMoviesUseCase(1) } returns flow {
            emit(Result.Loading)
            emit(Result.Success(listOf(createMockMovie(10, "Popular Movie"))))
        }

        viewModel.resetSearch()
        advanceUntilIdle()

        state = viewModel.uiState.value
        assertFalse(state.isSearchMode)
        assertEquals("", state.searchQuery)
        assertTrue(state.autocompleteSuggestions.isEmpty())
        assertFalse(state.showAutocomplete)
        // After loadMovies() loads page 1, currentPage becomes 2
        assertEquals(2, state.currentPage)
    }

    @Test
    fun `selectAutocompleteSuggestion triggers search and hides autocomplete`() = runTest {
        advanceUntilIdle()
        val movie = createMockMovie(1, "Batman")
        val searchResults = listOf(movie)

        // First set up autocomplete suggestions
        coEvery { searchMoviesUseCase("bat", 1) } returns flow {
            emit(Result.Loading)
            emit(Result.Success(listOf(movie)))
        }
        viewModel.getAutocompleteSuggestions("bat")
        advanceUntilIdle()

        var state = viewModel.uiState.value
        assertTrue(state.showAutocomplete)

        // Mock full search when suggestion is selected
        coEvery { searchMoviesUseCase("Batman", 1) } returns flow {
            emit(Result.Loading)
            emit(Result.Success(searchResults))
        }

        viewModel.selectAutocompleteSuggestion(movie)
        advanceUntilIdle()

        state = viewModel.uiState.value
        assertEquals("Batman", state.searchQuery)
        assertFalse(state.showAutocomplete)
        assertTrue(state.autocompleteSuggestions.isEmpty())
        assertTrue(state.isSearchMode)
        assertEquals(1, state.movies.size)
    }

    @Test
    fun `searchMovies with blank query resets to popular movies`() = runTest {
        advanceUntilIdle()
        val query = "test"
        val searchResults = listOf(createMockMovie(1, "Test Movie"))

        // Set up search mode first
        coEvery { searchMoviesUseCase(query, 1) } returns flow {
            emit(Result.Loading)
            emit(Result.Success(searchResults))
        }
        viewModel.searchMovies(query)
        advanceUntilIdle()

        var state = viewModel.uiState.value
        assertTrue(state.isSearchMode)

        // Mock popular movies for reset
        coEvery { getPopularMoviesUseCase(1) } returns flow {
            emit(Result.Loading)
            emit(Result.Success(listOf(createMockMovie(10, "Popular Movie"))))
        }

        viewModel.searchMovies("")
        advanceUntilIdle()

        state = viewModel.uiState.value
        assertFalse(state.isSearchMode)
        assertEquals("", state.searchQuery)
        // After loadMovies() loads page 1, currentPage becomes 2
        assertEquals(2, state.currentPage)
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