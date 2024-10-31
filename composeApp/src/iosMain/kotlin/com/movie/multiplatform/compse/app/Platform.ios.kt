package com.movie.multiplatform.compse.app

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val os: String = "iOS"
    override val name: String =
            UIDevice.currentDevice.systemName() + " " +UIDevice.currentDevice.systemVersion    + ", " +
            UIDevice.currentDevice.name
    override val httpClientEngine: HttpClientEngine = Darwin.create {
        configureSession {
            timeoutIntervalForRequest = 10.0
            timeoutIntervalForResource = 15.0
        }
    }
}

actual fun getPlatform(): Platform = IOSPlatform()