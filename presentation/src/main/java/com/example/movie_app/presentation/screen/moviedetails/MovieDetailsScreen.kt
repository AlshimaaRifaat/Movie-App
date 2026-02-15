package com.example.movie_app.presentation.screen.moviedetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.movie_app.domain.model.Genre
import com.example.movie_app.domain.model.MovieDetails
import com.example.movie_app.domain.model.ProductionCompany
import com.example.movie_app.presentation.designsystem.components.LoadingIndicator
import com.example.movie_app.presentation.designsystem.components.ErrorMessage
import com.example.movie_app.presentation.designsystem.theme.MovieAppTheme
import com.example.movie_app.presentation.viewmodel.MovieDetailsViewModel

/**
 * Screen displaying detailed information about a movie.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    movieId: Int,
    onBackClick: () -> Unit,
    viewModel: MovieDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(movieId) {
        viewModel.loadMovieDetails(movieId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Movie Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingIndicator()
                }
                uiState.error != null -> {
                    ErrorMessage(
                        message = uiState.error ?: "An error occurred",
                        onRetry = { viewModel.retry(movieId) }
                    )
                }
                uiState.movieDetails != null -> {
                    val movie = uiState.movieDetails!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Backdrop image
                        AsyncImage(
                            model = movie.backdropUrl,
                            contentDescription = movie.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentScale = ContentScale.Crop
                        )

                        // Movie details
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = movie.title,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold
                            )

                            val tagline = movie.tagline
                            if (tagline != null && tagline.isNotBlank()) {
                                Text(
                                    text = tagline,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Rating: ${movie.voteAverage}/10",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = "Votes: ${movie.voteCount}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    if (movie.runtime != null) {
                                        Text(
                                            text = "Runtime: ${movie.runtime} minutes",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    if (movie.releaseDate != null) {
                                        Text(
                                            text = "Release Date: ${movie.releaseDate}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }

                            if (movie.genres.isNotEmpty()) {
                                Text(
                                    text = "Genres: ${movie.genres.joinToString(", ") { it.name }}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Text(
                                text = "Overview",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = movie.overview,
                                style = MaterialTheme.typography.bodyLarge
                            )

                            if (movie.productionCompanies.isNotEmpty()) {
                                Text(
                                    text = "Production Companies",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                                movie.productionCompanies.forEach { company ->
                                    Text(
                                        text = "• ${company.name}",
                                        style = MaterialTheme.typography.bodyMedium
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Movie Details - Content")
@Composable
private fun MovieDetailsScreenPreview() {
    MovieAppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Movie Details") },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // Backdrop image
                AsyncImage(
                    model = "https://image.tmdb.org/t/p/w1280/hqkIcbrOHL86JcnxY3YeQ4qoqA1.jpg",
                    contentDescription = "The Dark Knight",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Crop
                )

                // Movie details
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "The Dark Knight",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Why So Serious?",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Rating: 9.0/10",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Votes: 25000",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Runtime: 152 minutes",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Release Date: 2008-07-18",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Text(
                        text = "Genres: Action, Crime, Drama",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "Overview",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        text = "Production Companies",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = "• Warner Bros. Pictures",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "• Legendary Pictures",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Movie Details - Loading")
@Composable
private fun MovieDetailsScreenLoadingPreview() {
    MovieAppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Movie Details") },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LoadingIndicator()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Movie Details - Error")
@Composable
private fun MovieDetailsScreenErrorPreview() {
    MovieAppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Movie Details") },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                ErrorMessage(
                    message = "No internet connection. Please check your network settings and try again.",
                    onRetry = {}
                )
            }
        }
    }
}

