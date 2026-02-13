package com.example.movie_app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movie_app.domain.model.MovieDetails
import com.example.movie_app.domain.usecase.GetMovieDetailsUseCase
import com.example.movie_app.presentation.util.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the movie details screen.
 */
data class MovieDetailsUiState(
    val movieDetails: MovieDetails? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel for the movie details screen.
 * Implements MVVM architecture pattern.
 */
@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieDetailsUiState())
    val uiState: StateFlow<MovieDetailsUiState> = _uiState.asStateFlow()

    /**
     * Loads movie details for the given movie ID.
     */
    fun loadMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            getMovieDetailsUseCase(movieId)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = ErrorHandler.getErrorMessage(e)
                    )
                }
                .collect { result ->
                    if (result.isSuccess) {
                        val details = result.getOrNull()
                        _uiState.value = _uiState.value.copy(
                            movieDetails = details,
                            isLoading = false,
                            error = null // Clear any previous errors on success
                        )
                    } else {
                        val error = result.exceptionOrNull()
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = ErrorHandler.getErrorMessage(error)
                        )
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
     * Retries loading movie details.
     */
    fun retry(movieId: Int) {
        loadMovieDetails(movieId)
    }
}

