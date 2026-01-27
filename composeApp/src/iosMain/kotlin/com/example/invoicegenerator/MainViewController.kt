package com.example.invoicegenerator

import androidx.compose.ui.window.ComposeUIViewController
import com.example.invoicegenerator.di.initKoin
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    App()
}

fun initKoinIOS() {
    initKoin()
}
