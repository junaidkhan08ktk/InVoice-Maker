package com.example.invoicegenerator.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import com.example.invoicegenerator.viewmodel.SettingsViewModel
import org.jetbrains.compose.resources.stringResource
import com.example.invoicegenerator.Res
import com.example.invoicegenerator.*
import com.example.invoicegenerator.select_language

data class Language(
    val code: String,
    val name: String,
    val nativeName: String,
    val defaultCurrency: String,
    val flag: String
)

val supportedLanguages = listOf(
    Language("en", "English",    "English",    "USD", "🇺🇸"),
    Language("hi", "Hindi",      "हिन्दी",       "INR", "🇮🇳"),
    Language("ur", "Urdu",       "اردو",        "PKR", "🇵🇰"),
    Language("es", "Spanish",    "Español",    "EUR", "🇪🇸"),
    Language("fr", "French",     "Français",   "EUR", "🇫🇷"),
    Language("ar", "Arabic",     "العربية",     "SAR", "🇸🇦"),
    Language("bn", "Bengali",    "বাংলা",       "BDT", "🇧🇩"),
    Language("pt", "Portuguese", "Português",  "BRL", "🇧🇷"),
    Language("ru", "Russian",    "Русский",    "RUB", "🇷🇺"),
    Language("de", "German",     "Deutsch",    "EUR", "🇩🇪")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectionScreen(
    onLanguageSelected: () -> Unit,
) {

    val viewModel: SettingsViewModel=koinViewModel()
    val currentLanguage by viewModel.language.collectAsState(initial = "en")

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(Res.string.select_language)) })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            items(supportedLanguages) { language ->
                val isSelected = currentLanguage == language.code
                ListItem(
                    headlineContent = {
                        Text(
                            text = language.nativeName,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    supportingContent = { Text(language.name) },
                    leadingContent = {
                        Text(
                            text = language.flag,
                            fontSize = 28.sp
                        )
                    },
                    trailingContent = {
                        if (isSelected) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = if (isSelected)
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        else
                            MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.clickable {
                        viewModel.setLanguage(language.code)
                        viewModel.setCurrency(language.defaultCurrency)
                        getPlatform().setLanguage(language.code)
                        onLanguageSelected()
                    }
                )
                HorizontalDivider()
            }
        }
    }
}
