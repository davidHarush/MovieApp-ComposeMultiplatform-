package com.movie.multiplatform.compse.app.network

object MovieHelper {

    private const val BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w500"

    fun getImage(imageUrl: String) =
        (BASE_IMAGE_URL + imageUrl).apply {
            println("Image URL: $this")

    }

}