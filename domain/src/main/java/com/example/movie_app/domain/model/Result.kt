package com.example.movie_app.domain.model

/**
 * A generic class that holds a value with its loading status
 * Represents the result of a business operation following SOLID principles
 */
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable, val message: String? = null) : Result<Nothing>()
    data object Loading : Result<Nothing>()

    /**
     * Returns data if the result is Success, null otherwise
     */
    fun getDataOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    /**
     * Returns true if the result is Success
     */
    val isSuccess: Boolean
        get() = this is Success

    /**
     * Returns true if the result is Error
     */
    val isError: Boolean
        get() = this is Error

    /**
     * Returns true if the result is Loading
     */
    val isLoading: Boolean
        get() = this is Loading
}

