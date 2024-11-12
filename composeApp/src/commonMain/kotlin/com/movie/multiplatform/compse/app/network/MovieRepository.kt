package com.movie.multiplatform.compse.app.network

import com.movie.multiplatform.compse.app.getPlatform
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable

enum class MovieFetchType(val fetchFunction: suspend MovieRepository.(page: Int) -> MovieResponse) {
    POPULAR({ page -> getPopularMovies(page) }),
    TOP_RATED({ page -> getTopRatedMovies(page) }),
    UPCOMING({ page -> getUpcomingMovies(page) }),
    NOW_PLAYING({ page -> getNowPlayingMovies(page) })
}


class MovieRepository {

    private val apiKeyVal = "56a778f90174e0061b6e7c69a5e3c9f2"

    private val baseUrl = "https://api.themoviedb.org/3"
    private val moviesUrl = "$baseUrl/movie"
    private val discoverUrl = "$baseUrl/discover/movie"
    private val searchUrl = "$baseUrl/search/movie"
    private val genreUrl = "$baseUrl/genre/movie/list"
    private val apiKey = "api_key"

    //https://api.themoviedb.org/3/collection/{collection_id}
    private val collectionUrl = "$baseUrl/collection"

    private val client = HttpClient(getPlatform().httpClientEngine) {
        install(ContentNegotiation) {
            json(
                kotlinx.serialization.json.Json {
                    ignoreUnknownKeys = true
                }
            )
        }
        install(Logging) {
            level = LogLevel.BODY
        }

        defaultRequest {
            // not working well - to be fixed
            println("defaultRequest: $url")
            println("defaultRequest: ${url.host}")
            if (url.host == "secure.api.com") {
                headers.append("Authorization", "Bearer your_token")
            }
        }
    }


    private suspend fun HttpRequestBuilder.addAuthHeaderIfNeeded() {
        println("addAuthHeaderIfNeeded : defaultRequest: $url")
        println("addAuthHeaderIfNeeded: defaultRequest: ${url.host}")
        if (this.url.host == "secure.api.com") {
            headers.append("Authorization", "Bearer your_token")
        }
    }




    private fun HttpRequestBuilder.addApiKey() {
        parameter(apiKey, apiKeyVal)
    }

    // Fetch movies by different categories
    suspend fun getPopularMovies(page: Int = 0): MovieResponse = fetchMoviesByCategory("popular", page)
    suspend fun getTopRatedMovies(page: Int = 0): MovieResponse = fetchMoviesByCategory("top_rated", page)
    suspend fun getUpcomingMovies(page: Int = 0): MovieResponse = fetchMoviesByCategory("upcoming",page)
    suspend fun getNowPlayingMovies(page: Int = 0): MovieResponse = fetchMoviesByCategory("now_playing",page)


    // Private helper function to avoid repeating code
    private suspend fun fetchMoviesByCategory(category: String , page: Int): MovieResponse {
        return client.get("$moviesUrl/$category") {
            addApiKey()
            addAuthHeaderIfNeeded()
            parameter("page", page)

        }.body()
    }

    // Fetch movie details by ID
    suspend fun getMovieDetails(movieId: Int): MovieDetails {
        return client.get("$moviesUrl/$movieId") {
            addApiKey()
            addAuthHeaderIfNeeded()
        }.body()
    }

    // Search for movies by keyword
    suspend fun searchMovies(query: String): MovieResponse {
        return client.get(searchUrl) {
            addApiKey()
            parameter("query", query)
            addAuthHeaderIfNeeded()
        }.body()
    }

    // Get list of genres
    suspend fun getGenres(): GenreResponse {
        return client.get(genreUrl) {
            addApiKey()
            addAuthHeaderIfNeeded()
        }.body()
    }

    // Get movies by genre
    suspend fun getMoviesByGenre(genreId: Int): MovieResponse {
        return client.get(discoverUrl) {
            addApiKey()
            parameter("with_genres", genreId)
            addAuthHeaderIfNeeded()
        }.body()
    }

    // Get movies by year
    suspend fun getMoviesByYear(year: Int): MovieResponse {
        return client.get(discoverUrl) {
            addApiKey()
            parameter("primary_release_year", year)
        }.body()
    }


    //https://api.themoviedb.org/3/movie/{movie_id}/images
    suspend fun getMovieImages(movieId: Int): MovieImagesResponse? {
        return client.get("$moviesUrl/$movieId/images") {
            addApiKey()
            addAuthHeaderIfNeeded()
        }.body()

    }

    //https://api.themoviedb.org/3/movie/{movie_id}/credits
    suspend fun getMovieCast(movieId: Int): CreditsResponse? {
        return client.get("$moviesUrl/$movieId/credits") {
            addApiKey()
            addAuthHeaderIfNeeded()
        }.body()
    }

    // https://api.themoviedb.org/3/collection/{collection_id}
    suspend fun getCollectionDetails(collectionId: Int): MovieCollectionResponse? {
        return client.get("$collectionUrl/$collectionId") {
            addApiKey()
            addAuthHeaderIfNeeded()
        }.body()

    }
}
