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

class MovieRepository {

    private val apiKeyVal = "56a778f90174e0061b6e7c69a5e3c9f2"

    private val baseUrl = "https://api.themoviedb.org/3"
    private val moviesUrl = "$baseUrl/movie"
    private val discoverUrl = "$baseUrl/discover/movie"
    private val searchUrl = "$baseUrl/search/movie"
    private val genreUrl = "$baseUrl/genre/movie/list"
    private val apiKey = "api_key"

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
    suspend fun getPopularMovies(): MovieResponse = fetchMoviesByCategory("popular")
    suspend fun getTopRatedMovies(): MovieResponse = fetchMoviesByCategory("top_rated")
    suspend fun getUpcomingMovies(): MovieResponse = fetchMoviesByCategory("upcoming")
    suspend fun getNowPlayingMovies(): MovieResponse = fetchMoviesByCategory("now_playing")

    // Fetch movie details by ID
    suspend fun getMovieDetails(movieId: Int): Movie {
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
        }.body()
    }

    // Get list of genres
    suspend fun getGenres(): GenreResponse {
        return client.get(genreUrl) {
            addApiKey()
        }.body()
    }

    // Get movies by genre
    suspend fun getMoviesByGenre(genreId: Int): MovieResponse {
        return client.get(discoverUrl) {
            addApiKey()
            parameter("with_genres", genreId)
        }.body()
    }

    // Get movies by year
    suspend fun getMoviesByYear(year: Int): MovieResponse {
        return client.get(discoverUrl) {
            addApiKey()
            parameter("primary_release_year", year)
        }.body()
    }

    // Get movies sorted by criteria
    suspend fun getMoviesSortedBy(sortBy: String): MovieResponse {
        return client.get(discoverUrl) {
            addApiKey()
            parameter("sort_by", sortBy)
        }.body()
    }

    // Private helper function to avoid repeating code
    private suspend fun fetchMoviesByCategory(category: String): MovieResponse {
        return client.get("$moviesUrl/$category") {
            addApiKey()
            addAuthHeaderIfNeeded()
        }.body()
    }
}
