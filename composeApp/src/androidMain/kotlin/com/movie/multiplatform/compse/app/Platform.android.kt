package com.movie.multiplatform.compse.app

import android.os.Build
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