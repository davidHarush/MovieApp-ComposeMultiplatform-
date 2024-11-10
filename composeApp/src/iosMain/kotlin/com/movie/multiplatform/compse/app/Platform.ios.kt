package com.movie.multiplatform.compse.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import platform.UIKit.UIDevice

class IOSPlatform : Platform {
    override val os: String = "iOS"
    override val name: String =
        UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion + ", " +
                UIDevice.currentDevice.name
    override val httpClientEngine: HttpClientEngine = Darwin.create {
        configureSession {
            timeoutIntervalForRequest = 10.0
            timeoutIntervalForResource = 15.0
        }
    }
}

actual fun getPlatform(): Platform = IOSPlatform()


@Composable
actual fun setSystemBarColor(color: Color) {
//    val uiColor = UIColor(
//        red = color.red.toDouble(),
//        green = color.green.toDouble(),
//        blue = color.blue.toDouble(),
//        alpha = color.alpha.toDouble()
//    )
//
//    StatusBarHelper.setStatusBarStyle(darkIcons = false, color = uiColor)
}
