package com.movie.multiplatform.compse.app.network

import com.movie.multiplatform.compse.app.getPlatform
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json


enum class MovieFetchType(val fetchFunction: suspend (MovieRepository, Int) -> MovieResponse) {
    POPULAR({ repo, page -> repo.getPopularMovies(page) }),
    TOP_RATED({ repo, page -> repo.getTopRatedMovies(page) }),
    UPCOMING({ repo, page -> repo.getUpcomingMovies(page) }),
    NOW_PLAYING({ repo, page -> repo.getNowPlayingMovies(page) })
}


class MovieRepository {

    private val apiKeyVal = "Your API Key"

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
            println("defaultRequest: $url")
            println("defaultRequest: ${url.host}")
            if (url.host == "secure.api.com") {
                headers.append("Authorization", "Bearer your_token")
            }
        }
    }


    private suspend fun HttpRequestBuilder.addAuthHeaderIfNeeded() {
        if (this.url.host == "secure.api.com") {
            headers.append("Authorization", "Bearer your_token")
        }
    }

    private fun HttpRequestBuilder.addApiKey() {
        parameter(apiKey, apiKeyVal)
    }

    private fun HttpRequestBuilder.addLanguage() {
        parameter("language", "en")
    }

    private fun HttpRequestBuilder.addPage(page: Int) {
        parameter("page", page)
    }
    private fun HttpRequestBuilder.addVoteAverage() {
        parameter("vote_average.gte", 6)
    }

    private fun HttpRequestBuilder.addVoteCount() {
        parameter("vote_count.gte", 500)
    }

    private fun HttpRequestBuilder.addAll() {
        addApiKey()
        addLanguage()
        addVoteAverage()
        addVoteCount()
    }


    suspend fun getPopularMovies(page: Int): MovieResponse = fetchMoviesByCategory("popular", page)
    suspend fun getTopRatedMovies(page: Int): MovieResponse =
        fetchMoviesByCategory("top_rated", page)

    suspend fun getUpcomingMovies(page: Int): MovieResponse =
        fetchMoviesByCategory("upcoming", page)

    suspend fun getNowPlayingMovies(page: Int): MovieResponse =
        fetchMoviesByCategory("now_playing", page)


    suspend fun getMovieDetails(movieId: Int): MovieDetails {
        return client.get("$moviesUrl/$movieId") {
            addApiKey()
            parameter("language", "en")
        }.body()
    }


    suspend fun getCollectionDetails(collectionId: Int): MovieCollectionResponse {
        return client.get("$baseUrl/collection/$collectionId") {
            addApiKey()
            parameter("language", "en")
        }.body()
    }

    suspend fun getMovieCast(movieId: Int): List<CastMember> {
        val response: CreditsResponse = client.get("$moviesUrl/$movieId/credits") {
            addApiKey()
            parameter("language", "en")
        }.body()
        return response.cast
    }


    suspend fun getMovieImages(movieId: Int): MovieImagesResponse {
        val response: MovieImagesResponse = client.get("$moviesUrl/$movieId/images") {
            addApiKey()
            parameter("language", "en")
        }.body()
        return response

    }



    suspend fun searchMovies(query: String): MovieResponse {
        return client.get(searchUrl) {
            addApiKey()
            parameter("query", query)
        }.body()
    }

    suspend fun getGenres(): GenreResponse {
        return client.get(genreUrl) {
            addApiKey()
        }.body()
    }

    suspend fun getMoviesByGenre(genreId: Int): MovieResponse {
        return client.get(discoverUrl) {
            addApiKey()
            parameter("with_genres", genreId)
        }.body()
    }

    suspend fun getMoviesByYear(year: Int): MovieResponse {
        return client.get(discoverUrl) {
            addApiKey()
            parameter("primary_release_year", year)
        }.body()
    }


    suspend fun getMoviesSortedBy(sortBy: String): MovieResponse {
        return client.get(discoverUrl) {
            addApiKey()
            parameter("sort_by", sortBy)
        }.body()
    }

    private suspend fun fetchMoviesByCategory(category: String, page: Int = 1): MovieResponse {
        return client.get("$moviesUrl/$category") {
            addAll()
            addAuthHeaderIfNeeded()
            addPage(page)

        }.body()
    }

}
