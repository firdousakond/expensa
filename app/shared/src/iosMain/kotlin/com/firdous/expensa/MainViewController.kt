package com.firdous.expensa

import androidx.compose.ui.window.ComposeUIViewController
import com.firdous.expensa.di.initKoin

private var koinStarted = false

fun MainViewController() =
    run {
        if (!koinStarted) {
            initKoin()
            koinStarted = true
        }
        ComposeUIViewController { App() }
    }
