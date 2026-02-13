package com.example.movie_app.presentation.util

import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Utility class for handling and formatting errors.
 * Provides user-friendly error messages based on exception types.
 */
object ErrorHandler {
    /**
     * Converts an exception to a user-friendly error message.
     */
    fun getErrorMessage(throwable: Throwable?): String {
        return when (throwable) {
            is UnknownHostException -> 
                "No internet connection. Please check your network settings and try again."
            is SocketTimeoutException -> 
                "Connection timeout. Please check your internet connection and try again."
            is IOException -> 
                "Network error. Please check your internet connection and try again."
            null -> 
                "An unexpected error occurred. Please try again."
            else -> 
                throwable.message ?: "An error occurred. Please try again."
        }
    }
}

