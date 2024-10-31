package com.movie.multiplatform.compse.app

import androidx.compose.ui.graphics.ImageBitmap
import io.ktor.client.engine.HttpClientEngine

interface Platform {
    val os : String
    val name: String
    val httpClientEngine: HttpClientEngine

}
expect fun getPlatform(): Platform