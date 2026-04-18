package com.example.invoicegenerator.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import org.koin.compose.viewmodel.koinViewModel
import com.example.invoicegenerator.ui.navigation.Screen
import com.example.invoicegenerator.viewmodel.BusinessViewModel
import com.example.invoicegenerator.viewmodel.SettingsViewModel
import org.jetbrains.compose.resources.stringResource
import com.example.invoicegenerator.Res
import com.example.invoicegenerator.*
import com.example.invoicegenerator.settings
import com.example.invoicegenerator.language_currency
import com.example.invoicegenerator.business_profile
import com.example.invoicegenerator.gstin
import com.example.invoicegenerator.about
import com.example.invoicegenerator.privacy_policy
import com.example.invoicegenerator.terms_conditions
import com.example.invoicegenerator.version
import com.example.invoicegenerator.rate_app
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateTo: (String) -> Unit,
    onEditBusiness: () -> Unit,

    ) {
    val viewModel: BusinessViewModel = koinViewModel()
    val settingsViewModel: SettingsViewModel = koinViewModel()
    val businessProfile by viewModel.businessProfile.collectAsState()
    val currentLanguage by settingsViewModel.language.collectAsState(initial = "en")
    val currentCurrency by settingsViewModel.currency.collectAsState(initial = "USD")

    val languageName = supportedLanguages.find { it.code == currentLanguage }?.name ?: "English"

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(Res.string.settings)) }) },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = Screen.Settings.route,
                onNavigate = onNavigateTo
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {


            // App Settings
            Text(text = "App Settings", style = MaterialTheme.typography.titleMedium)
            Card(modifier = Modifier.fillMaxWidth()) {
                ListItem(
                    headlineContent = { Text(stringResource(Res.string.language_currency)) },
                    supportingContent = { Text("$languageName ($currentCurrency)") },
                    leadingContent = {
                        Icon(
                            painterResource(Res.drawable.ic_language_currency),
                            contentDescription = null
                        )
                    },
                    trailingContent = {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.clickable { onNavigateTo(Screen.LanguageSelection.route) }
                )
            }

            // Business Profile
            Text(
                text = stringResource(Res.string.business_profile),
                style = MaterialTheme.typography.titleMedium
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                ListItem(
                    headlineContent = { Text(businessProfile?.name ?: "No Business Setup") },
                    supportingContent = { Text("${stringResource(Res.string.gstin)}: ${businessProfile?.gstin ?: "N/A"}") },
                    leadingContent = { Icon(Icons.Default.Person, contentDescription = null) },
                    trailingContent = {
                        IconButton(onClick = onEditBusiness) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                        }
                    }
                )
            }

            // Legal & About
            val context = androidx.compose.ui.platform.LocalContext.current
            Text(
                text = stringResource(Res.string.about),
                style = MaterialTheme.typography.titleMedium
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                Column {
                    ListItem(
                        headlineContent = { Text(stringResource(Res.string.privacy_policy)) },
                        leadingContent = { Icon(Icons.Default.Lock, contentDescription = null) },
                        modifier = Modifier.clickable {
                            val intent = android.content.Intent(
                                android.content.Intent.ACTION_VIEW,
                                "https://sites.google.com/d/1lGZSQ6bljzkqu_GIf7Di3DSJrVaqBdDC/p/1b6HC2swyULjSHGH2rFlRJhjuAXsAL5gU/edit".toUri()
                            )
                            context.startActivity(intent)
                        }
                    )
                    HorizontalDivider()

                    ListItem(
                        headlineContent = { Text(stringResource(Res.string.rate_app)) },
                        leadingContent = { Icon(Icons.Default.Star, contentDescription = null) },
                        modifier = Modifier.clickable {
                            getPlatform().rateApp()
                        }
                    )
                }
            }

            Text(
                text = stringResource(Res.string.version),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
