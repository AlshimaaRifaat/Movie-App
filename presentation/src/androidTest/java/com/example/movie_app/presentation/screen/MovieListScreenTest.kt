package com.example.movie_app.presentation.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.movie_app.presentation.designsystem.theme.MovieAppTheme
import com.example.movie_app.presentation.screen.movielist.MovieListScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for MovieListScreen.
 */
@RunWith(AndroidJUnit4::class)
class MovieListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun movieListScreen_displaysTitle() {
        composeTestRule.setContent {
            MovieAppTheme {
                MovieListScreen(
                    onMovieClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Movies")
            .assertIsDisplayed()
    }

    @Test
    fun movieListScreen_displaysSearchBar() {
        composeTestRule.setContent {
            MovieAppTheme {
                MovieListScreen(
                    onMovieClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Search movies...")
            .assertIsDisplayed()
    }

    @Test
    fun movieListScreen_allowsSearchInput() {
        composeTestRule.setContent {
            MovieAppTheme {
                MovieListScreen(
                    onMovieClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Search movies...")
            .performTextInput("test query")
    }
}

