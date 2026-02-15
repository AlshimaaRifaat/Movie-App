package com.example.movie_app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movie_app.domain.model.Movie
import com.example.movie_app.domain.model.Result
import com.example.movie_app.domain.usecase.GetPopularMoviesUseCase
import com.example.movie_app.domain.usecase.SearchMoviesUseCase
import com.example.movie_app.presentation.util.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the movie list screen.
 */
data class MovieListUiState(
    val movies: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 1,
    val hasMore: Boolean = true,
    val searchQuery: String = "",
    val isSearchMode: Boolean = false,
    val autocompleteSuggestions: List<Movie> = emptyList(),
    val showAutocomplete: Boolean = false
)

/**
 * ViewModel for the movie list screen.
 * Implements MVVM architecture pattern.
 */
@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieListUiState())
    val uiState: StateFlow<MovieListUiState> = _uiState.asStateFlow()

    init {
        loadMovies()
    }

    /**
     * Loads popular movies for the current page.
     */
    fun loadMovies() {
        if (_uiState.value.isLoading || !_uiState.value.hasMore) return

        viewModelScope.launch {
            val page = _uiState.value.currentPage
            val useCase = if (_uiState.value.isSearchMode) {
                searchMoviesUseCase(_uiState.value.searchQuery, page)
            } else {
                getPopularMoviesUseCase(page)
            }

            useCase
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = ErrorHandler.getErrorMessage(e)
                    )
                }
                .collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = true,
                                error = null
                            )
                        }
                        is Result.Success -> {
                            val movies = result.data
                            _uiState.value = _uiState.value.copy(
                                movies = _uiState.value.movies + movies,
                                isLoading = false,
                                currentPage = page + 1,
                                hasMore = movies.isNotEmpty(),
                                error = null // Clear any previous errors on success
                            )
                        }
                        is Result.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = ErrorHandler.getErrorMessage(result.exception)
                            )
                        }
                    }
                }
        }
    }

    /**
     * Searches for movies with the given query.
     */
    fun searchMovies(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                // Reset to popular movies mode.
                // Important: isLoading must be false before calling loadMovies(),
                // otherwise loadMovies() will early-return and we end up stuck loading.
                _uiState.value = _uiState.value.copy(
                    searchQuery = "",
                    isSearchMode = false,
                    movies = emptyList(),
                    currentPage = 1,
                    hasMore = true,
                    isLoading = false,
                    error = null
                )
                loadMovies()
                return@launch
            }

            _uiState.value = _uiState.value.copy(
                searchQuery = query,
                isSearchMode = true,
                movies = emptyList(),
                currentPage = 1,
                hasMore = true,
                error = null
            )

            searchMoviesUseCase(query, 1)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = ErrorHandler.getErrorMessage(e)
                    )
                }
                .collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = true,
                                error = null
                            )
                        }
                        is Result.Success -> {
                            val movies = result.data
                            _uiState.value = _uiState.value.copy(
                                movies = movies,
                                isLoading = false,
                                currentPage = 2,
                                hasMore = movies.isNotEmpty(),
                                error = null // Clear any previous errors on success
                            )
                        }
                        is Result.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = ErrorHandler.getErrorMessage(result.exception)
                            )
                        }
                    }
                }
        }
    }

    /**
     * Clears the error state.
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Retries loading movies.
     */
    fun retry() {
        if (_uiState.value.isSearchMode) {
            searchMovies(_uiState.value.searchQuery)
        } else {
            loadMovies()
        }
    }

    private var autocompleteJob: kotlinx.coroutines.Job? = null

    /**
     * Gets autocomplete suggestions for the given query.
     * Limits results to first 5 suggestions for better UX.
     * Cancels previous autocomplete requests to prevent race conditions.
     */
    fun getAutocompleteSuggestions(query: String) {
        // Cancel previous autocomplete request
        autocompleteJob?.cancel()
        
        if (query.isBlank() || query.length < 2) {
            _uiState.value = _uiState.value.copy(
                autocompleteSuggestions = emptyList(),
                showAutocomplete = false
            )
            return
        }

        autocompleteJob = viewModelScope.launch {
            searchMoviesUseCase(query, 1)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        autocompleteSuggestions = emptyList(),
                        showAutocomplete = false
                    )
                }
                .collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            // Don't update suggestions while loading
                        }
                        is Result.Success -> {
                            val suggestions = result.data.take(5) // Limit to 5 suggestions
                            _uiState.value = _uiState.value.copy(
                                autocompleteSuggestions = suggestions,
                                showAutocomplete = suggestions.isNotEmpty()
                            )
                        }
                        is Result.Error -> {
                            _uiState.value = _uiState.value.copy(
                                autocompleteSuggestions = emptyList(),
                                showAutocomplete = false
                            )
                        }
                    }
                }
        }
    }

    /**
     * Hides the autocomplete dropdown and cancels any pending autocomplete requests.
     */
    fun hideAutocomplete() {
        autocompleteJob?.cancel()
        _uiState.value = _uiState.value.copy(
            showAutocomplete = false,
            autocompleteSuggestions = emptyList()
        )
    }

    /**
     * Resets search and returns to popular movies.
     * Clears autocomplete and cancels any pending requests.
     */
    fun resetSearch() {
        autocompleteJob?.cancel()
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            isSearchMode = false,
            movies = emptyList(),
            currentPage = 1,
            hasMore = true,
            isLoading = false,
            error = null,
            autocompleteSuggestions = emptyList(),
            showAutocomplete = false
        )
        loadMovies()
    }

    /**
     * Selects a movie from autocomplete suggestions.
     * Cancels any pending autocomplete requests and triggers full search.
     */
    fun selectAutocompleteSuggestion(movie: Movie) {
        autocompleteJob?.cancel()
        _uiState.value = _uiState.value.copy(
            searchQuery = movie.title,
            showAutocomplete = false,
            autocompleteSuggestions = emptyList()
        )
        searchMovies(movie.title)
    }
}

