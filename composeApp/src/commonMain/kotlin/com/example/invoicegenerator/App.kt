package com.example.invoicegenerator

import androidx.compose.runtime.Composable
import com.example.invoicegenerator.ui.navigation.NavGraph
import com.example.invoicegenerator.ui.theme.InVoiceGeneratorTheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        InVoiceGeneratorTheme {
            NavGraph()
        }
    }
}
