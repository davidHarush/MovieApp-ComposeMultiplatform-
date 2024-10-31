package com.movie.multiplatform.compse.app.network
import kotlinx.serialization.Serializable

@Serializable
data class MovieResponse(
    val results: List<Movie>
)

@Serializable
data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String
)

@Serializable
data class GenreResponse(
    val genres: List<Genre>
)

@Serializable
data class Genre(
    val id: Int,
    val name: String
)


