package com.example.movie_app.data.dto

import com.example.movie_app.domain.model.Genre
import com.example.movie_app.domain.model.MovieDetails
import com.example.movie_app.domain.model.ProductionCompany
import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for Movie Details API response.
 */
data class MovieDetailsDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("overview")
    val overview: String,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("release_date")
    val releaseDate: String?,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int,
    @SerializedName("popularity")
    val popularity: Double,
    @SerializedName("runtime")
    val runtime: Int?,
    @SerializedName("genres")
    val genres: List<GenreDto>,
    @SerializedName("production_companies")
    val productionCompanies: List<ProductionCompanyDto>,
    @SerializedName("tagline")
    val tagline: String?
) {
    /**
     * Maps DTO to domain model.
     */
    fun toDomain(): MovieDetails {
        return MovieDetails(
            id = id,
            title = title,
            overview = overview,
            posterPath = posterPath,
            backdropPath = backdropPath,
            releaseDate = releaseDate,
            voteAverage = voteAverage,
            voteCount = voteCount,
            popularity = popularity,
            runtime = runtime,
            genres = genres.map { it.toDomain() },
            productionCompanies = productionCompanies.map { it.toDomain() },
            tagline = tagline
        )
    }
}

/**
 * DTO for Genre.
 */
data class GenreDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
) {
    fun toDomain(): Genre {
        return Genre(id = id, name = name)
    }
}

/**
 * DTO for Production Company.
 */
data class ProductionCompanyDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("logo_path")
    val logoPath: String?
) {
    fun toDomain(): ProductionCompany {
        return ProductionCompany(id = id, name = name, logoPath = logoPath)
    }
}

