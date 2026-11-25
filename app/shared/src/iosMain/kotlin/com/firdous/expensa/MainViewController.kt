package com.firdous.expensa

import androidx.compose.ui.window.ComposeUIViewController
import com.firdous.expensa.di.initKoin
import org.koin.core.context.GlobalContext

fun MainViewController() =
    run {
        if (GlobalContext.getOrNull() == null) initKoin()
        ComposeUIViewController { App() }
    }
