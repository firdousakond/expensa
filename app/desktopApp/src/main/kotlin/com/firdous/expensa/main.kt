package com.firdous.expensa

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.firdous.expensa.di.initKoin

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Expensa",
        ) {
            App()
        }
    }
}
