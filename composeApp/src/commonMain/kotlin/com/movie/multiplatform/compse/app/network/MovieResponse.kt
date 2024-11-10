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
    val poster_path: String,
    val release_date: String,
    val vote_average: Double,
    val runtime: Int? = null,
    val genres: List<Genre> = emptyList(),
    val backdrop_path: String? = null
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


//--------------- MovieDetailsResponse ----------------



@Serializable
data class MovieDetails(
    val id: Int,
    val title: String,
    val overview: String? = null,
    val release_date: String? = null,
    val vot_average: Double? = null,
    val belongs_to_collection: Collection? = null,
    val runtime: Int? = null,
    val genres: List<Genre>? = null,
    val budget: Int? = null,
    val revenue: Int? = null,
    val spokenLanguages: List<SpokenLanguage>? = null,
    val posterPath: String? = null,
    val backdrop_path: String? = null
)
@Serializable
data class ProductionCompany(val id: Int, val name: String, val logoPath: String? = null, val originCountry: String? = null)

@Serializable
data class ProductionCountry(val iso31661: String, val name: String)

@Serializable
data class SpokenLanguage(val iso6391: String, val name: String)

@Serializable
data class Collection(
    val id: Int,
    val name: String,
    val poster_path: String? = null,
    val backdrop_path: String? = null
)

//------------ MovieCreditsResponse ------------

@Serializable
data class CastMember(
    val id: Int,
    val name: String,
    val character: String,
    val profile_path: String? = null
)

@Serializable
data class CreditsResponse(
    val cast: List<CastMember>
)


//------------ MovieImagesResponse ----------------

@Serializable
data class MovieImagesResponse(
    val id: Int,
    val backdrops: List<ImageDetail>,
    val logos: List<ImageDetail>,
    val posters: List<ImageDetail>
)

@Serializable
data class ImageDetail(
    val aspect_ratio: Float,
    val height: Int,
    val iso_639_1: String?,
    val file_path: String,
    val vote_average: Float,
    val vote_count: Int,
    val width: Int
)

//------------ MovieCollectionResponse ----------------


@Serializable
data class MovieCollectionResponse(
    val id: Int? = null,
    val name: String? = null,
    val overview: String? = null,
    val poster_path: String? = null,
    val backdrop_path: String? = null,
    val parts: List<MoviePart>? = emptyList()
)

@Serializable
data class MoviePart(
    val adult: Boolean,
    val backdrop_path: String? = null,
    val id: Int? = null,
    val title: String,
    val original_language: String,
    val original_title: String,
    val overview: String,
    val poster_path: String? = null,
    val media_type: String,
    val genre_ids: List<Int>? = emptyList(),
    val popularity: Float,
    val release_date: String,
    val video: Boolean,
    val vote_average: Float,
    val vote_count: Int
)

