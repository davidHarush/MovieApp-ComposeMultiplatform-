package com.movie.multiplatform.compse.app

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import java.util.concurrent.TimeUnit

class AndroidPlatform : Platform {
    override val os: String = "Android"
    override val name: String = "Android ${Build.VERSION.SDK_INT}, ${Build.BRAND}, ${Build.MODEL}"
    override val httpClientEngine: HttpClientEngine = OkHttp.create {
        config {
            retryOnConnectionFailure(true)
            connectTimeout(5, TimeUnit.SECONDS)
            readTimeout(10, TimeUnit.SECONDS)
            writeTimeout(10, TimeUnit.SECONDS)
        }
    }
}

actual fun getPlatform(): Platform = AndroidPlatform()


@Composable
actual fun setSystemBarColor(color: Color) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = color,
        darkIcons = color.luminance() > 0.5f
    )
}