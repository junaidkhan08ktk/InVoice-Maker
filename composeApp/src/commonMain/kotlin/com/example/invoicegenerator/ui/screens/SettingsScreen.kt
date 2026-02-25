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
import com.example.invoicegenerator.generated.resources.Res
import com.example.invoicegenerator.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateTo: (String) -> Unit,
    onEditBusiness: () -> Unit,
    viewModel: BusinessViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val businessProfile by viewModel.businessProfile.collectAsState()
    val currentLanguage by settingsViewModel.language.collectAsState(initial = "en")
    val currentCurrency by settingsViewModel.currency.collectAsState(initial = "USD")

    val languageName = supportedLanguages.find { it.code == currentLanguage }?.name ?: "English"

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(Res.string.settings)) }) },
        bottomBar = { BottomNavigationBar(currentRoute = Screen.Settings.route, onNavigate = onNavigateTo) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Subscription Card
            Card(
                onClick = { onNavigateTo("paywall") },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.Default.Star, contentDescription = null)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = stringResource(Res.string.upgrade_pro), style = MaterialTheme.typography.titleMedium)
                        Text(text = stringResource(Res.string.upgrade_desc), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // App Settings
            Text(text = "App Settings", style = MaterialTheme.typography.titleMedium)
            Card(modifier = Modifier.fillMaxWidth()) {
                ListItem(
                    headlineContent = { Text(stringResource(Res.string.language_currency)) },
                    supportingContent = { Text("$languageName ($currentCurrency)") },
                    leadingContent = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                    trailingContent = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                    modifier = Modifier.clickable { onNavigateTo(Screen.LanguageSelection.route) }
                )
            }

            // Business Profile
            Text(text = stringResource(Res.string.business_profile), style = MaterialTheme.typography.titleMedium)
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
            Text(text = stringResource(Res.string.about), style = MaterialTheme.typography.titleMedium)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column {
                    ListItem(
                        headlineContent = { Text(stringResource(Res.string.privacy_policy)) },
                        leadingContent = { Icon(Icons.Default.Lock, contentDescription = null) },
                        modifier = Modifier.clickable {
                            val intent = android.content.Intent(
                                android.content.Intent.ACTION_VIEW,
                                "https://example.com/privacy".toUri()
                            )
                            context.startActivity(intent)
                        }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text(stringResource(Res.string.terms_conditions)) },
                        leadingContent = { Icon(Icons.Default.Info, contentDescription = null) },
                        modifier = Modifier.clickable {
                            val intent = android.content.Intent(
                                android.content.Intent.ACTION_VIEW,
                                "https://example.com/terms".toUri()
                            )
                            context.startActivity(intent)
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
