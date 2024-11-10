package com.movie.multiplatform.compse.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.ktor.client.engine.HttpClientEngine

interface Platform {
    val os: String
    val name: String
    val httpClientEngine: HttpClientEngine

}

expect fun getPlatform(): Platform


@Composable
expect fun setSystemBarColor(color: Color)
