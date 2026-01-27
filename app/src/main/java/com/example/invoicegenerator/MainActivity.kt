package com.example.invoicegenerator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.invoicegenerator.ui.navigation.NavGraph
import com.example.invoicegenerator.ui.theme.InVoiceGeneratorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InVoiceGeneratorTheme {
                NavGraph()
            }
        }
    }
}