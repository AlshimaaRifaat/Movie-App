package com.example.movie_app.domain.model

/**
 * Domain model representing detailed information about a Movie.
 */
data class MovieDetails(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val voteAverage: Double,
    val voteCount: Int,
    val popularity: Double,
    val runtime: Int?,
    val genres: List<Genre>,
    val productionCompanies: List<ProductionCompany>,
    val tagline: String?
) {
    val posterUrl: String
        get() = if (posterPath != null) {
            "https://image.tmdb.org/t/p/w500$posterPath"
        } else {
            ""
        }

    val backdropUrl: String
        get() = if (backdropPath != null) {
            "https://image.tmdb.org/t/p/w1280$backdropPath"
        } else {
            ""
        }
}

/**
 * Represents a movie genre.
 */
data class Genre(
    val id: Int,
    val name: String
)

/**
 * Represents a production company.
 */
data class ProductionCompany(
    val id: Int,
    val name: String,
    val logoPath: String?
) {
    val logoUrl: String
        get() = if (logoPath != null) {
            "https://image.tmdb.org/t/p/w200$logoPath"
        } else {
            ""
        }
}

