package com.example.movie_app.presentation.screen.movielist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.movie_app.presentation.designsystem.atoms.LoadingIndicator
import com.example.movie_app.presentation.designsystem.molecules.ErrorMessage
import com.example.movie_app.presentation.designsystem.organisms.MovieCard
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
                    else -> {
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

