package com.example.movie_app.presentation.screen.movielist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.movie_app.domain.model.Movie
import com.example.movie_app.presentation.designsystem.components.LoadingIndicator
import com.example.movie_app.presentation.designsystem.components.EmptyState
import com.example.movie_app.presentation.designsystem.components.ErrorMessage
import com.example.movie_app.presentation.designsystem.components.MovieCard
import com.example.movie_app.presentation.designsystem.theme.MovieAppTheme
import com.example.movie_app.presentation.viewmodel.MovieListViewModel
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

/**
 * Screen displaying a list of movies with infinite scrolling and search functionality.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    onMovieClick: (Int) -> Unit,
    viewModel: MovieListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val latestUiState by rememberUpdatedState(uiState)

    // Infinite scrolling: Load more when near the end
    val shouldLoadMore = remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            val totalItems = layoutInfo.totalItemsCount
            lastVisibleIndex >= totalItems - 3 && totalItems > 0
        }
    }

    LaunchedEffect(shouldLoadMore.value, uiState.isLoading, uiState.hasMore) {
        if (shouldLoadMore.value && !latestUiState.isLoading && latestUiState.hasMore) {
            viewModel.loadMovies()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Movies") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            var searchQuery by remember { mutableStateOf(uiState.searchQuery) }
            TextField(
                value = searchQuery,
                onValueChange = { newQuery ->
                    searchQuery = newQuery
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search movies...") },
                singleLine = true
            )

            // Debounced search
            LaunchedEffect(Unit) {
                snapshotFlow { searchQuery }
                    .map { it.trim() }
                    .distinctUntilChanged()
                    .drop(1) // ignore the initial value (prevents double-load on first composition / back navigation)
                    .debounce(500)
                    .filter { it != uiState.searchQuery }
                    .collect { viewModel.searchMovies(it) }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading && uiState.movies.isEmpty() -> {
                        LoadingIndicator()
                    }
                    uiState.error != null && uiState.movies.isEmpty() -> {
                        ErrorMessage(
                            message = uiState.error ?: "An error occurred",
                            onRetry = { viewModel.retry() }
                        )
                    }
                    uiState.movies.isEmpty() && !uiState.isLoading && uiState.isSearchMode -> {
                        // Empty search results
                        EmptyState(
                            message = "No movies found for \"${uiState.searchQuery}\".\n\nTry a different search term."
                        )
                    }
                    uiState.movies.isEmpty() && !uiState.isLoading && !uiState.isSearchMode -> {
                        // Empty popular movies (shouldn't happen, but handle gracefully)
                        EmptyState(
                            message = "No movies available at the moment.\n\nPlease try again later."
                        )
                    }
                    else -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Show error banner if there's an error but we have movies
                            if (uiState.error != null && uiState.movies.isNotEmpty()) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = uiState.error ?: "An error occurred",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onErrorContainer,
                                            modifier = Modifier.weight(1f)
                                        )
                                        TextButton(
                                            onClick = { viewModel.clearError() }
                                        ) {
                                            Text("Dismiss")
                                        }
                                    }
                                }
                            }
                            
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
                            ) {
                                items(
                                    items = uiState.movies,
                                    key = { it.id }
                                ) { movie ->
                                    MovieCard(
                                        movie = movie,
                                        onClick = { onMovieClick(movie.id) },
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }

                                // Loading indicator at the bottom for pagination
                                if (uiState.isLoading && uiState.movies.isNotEmpty()) {
                                    item {
                                        LoadingIndicator(
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Movie List - With Movies")
@Composable
private fun MovieListScreenPreview() {
    MovieAppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Movies") }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                TextField(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = { Text("Search movies...") },
                    singleLine = true
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
                ) {
                    items(
                        items = listOf(
                            Movie(
                                id = 1,
                                title = "The Dark Knight",
                                overview = "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.",
                                posterPath = "/qJ2tW6WMUDux911r6m7haRef0WH.jpg",
                                backdropPath = "/hqkIcbrOHL86JcnxY3YeQ4qoqA1.jpg",
                                releaseDate = "2008-07-18",
                                voteAverage = 9.0,
                                voteCount = 25000,
                                popularity = 100.0
                            ),
                            Movie(
                                id = 2,
                                title = "Inception",
                                overview = "A skilled thief is given a chance at redemption if he can pull off an impossible task: Inception.",
                                posterPath = "/oYuLEt3zVCKq57qu2F8dT7NIa6f.jpg",
                                backdropPath = "/s3TBrRGB1iav7gFOCNx3H31MoES.jpg",
                                releaseDate = "2010-07-16",
                                voteAverage = 8.8,
                                voteCount = 30000,
                                popularity = 95.0
                            )
                        ),
                        key = { it.id }
                    ) { movie ->
                        MovieCard(
                            movie = movie,
                            onClick = {},
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Movie List - Loading")
@Composable
private fun MovieListScreenLoadingPreview() {
    MovieAppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Movies") }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                TextField(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = { Text("Search movies...") },
                    singleLine = true
                )
                LoadingIndicator()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Movie List - Empty Search")
@Composable
private fun MovieListScreenEmptySearchPreview() {
    MovieAppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Movies") }
                )
            }
        ) { paddingValues ->
            Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
                TextField(
                    value = "test",
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = { Text("Search movies...") },
                    singleLine = true
                )
                EmptyState(
                    message = "No movies found for \"test\".\n\nTry a different search term."
                )
            }
        }
    }
}

