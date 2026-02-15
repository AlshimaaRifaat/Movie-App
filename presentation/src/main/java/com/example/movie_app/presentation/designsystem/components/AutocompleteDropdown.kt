package com.example.movie_app.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.movie_app.domain.model.Movie
import com.example.movie_app.presentation.designsystem.theme.MovieAppTheme

/**
 * Autocomplete dropdown component that displays search suggestions.
 * Shows a list of movies that match the search query.
 */
@Composable
fun AutocompleteDropdown(
    suggestions: List<Movie>,
    onSuggestionClick: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    if (suggestions.isEmpty()) return

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            suggestions.forEachIndexed { index, movie ->
                AutocompleteItem(
                    movie = movie,
                    onClick = { onSuggestionClick(movie) }
                )
                if (index < suggestions.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun AutocompleteItem(
    movie: Movie,
    onClick: () -> Unit
) {
    val releaseDate = movie.releaseDate
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (releaseDate != null) {
                Text(
                    text = releaseDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AutocompleteDropdownPreview() {
    MovieAppTheme {
        AutocompleteDropdown(
            suggestions = listOf(
                Movie(
                    id = 1,
                    title = "The Dark Knight",
                    overview = "Batman movie",
                    posterPath = "/qJ2tW6WMUDux911r6m7haRef0WH.jpg",
                    backdropPath = null,
                    releaseDate = "2008-07-18",
                    voteAverage = 9.0,
                    voteCount = 25000,
                    popularity = 100.0
                ),
                Movie(
                    id = 2,
                    title = "The Dark Knight Rises",
                    overview = "Batman movie sequel",
                    posterPath = null,
                    backdropPath = null,
                    releaseDate = "2012-07-20",
                    voteAverage = 8.4,
                    voteCount = 20000,
                    popularity = 90.0
                ),
                Movie(
                    id = 3,
                    title = "Batman Begins",
                    overview = "Batman origin story",
                    posterPath = null,
                    backdropPath = null,
                    releaseDate = "2005-06-15",
                    voteAverage = 8.2,
                    voteCount = 18000,
                    popularity = 85.0
                )
            ),
            onSuggestionClick = {}
        )
    }
}

