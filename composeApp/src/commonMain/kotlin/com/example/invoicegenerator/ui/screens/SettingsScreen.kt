package com.example.invoicegenerator.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import com.example.invoicegenerator.ui.navigation.Screen
import com.example.invoicegenerator.viewmodel.BusinessViewModel
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateTo: (String) -> Unit,
    onEditBusiness: () -> Unit,
    viewModel: BusinessViewModel = koinViewModel()
) {
    val businessProfile by viewModel.businessProfile.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Settings") }) },
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
                        Text(text = "Upgrade to Pro", style = MaterialTheme.typography.titleMedium)
                        Text(text = "Unlimited invoices, remove watermark, and more.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // Business Profile
            Text(text = "Business Profile", style = MaterialTheme.typography.titleMedium)
            Card(modifier = Modifier.fillMaxWidth()) {
                ListItem(
                    headlineContent = { Text(businessProfile?.name ?: "No Business Setup") },
                    supportingContent = { Text("GSTIN: ${businessProfile?.gstin ?: "N/A"}") },
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
            Text(text = "About", style = MaterialTheme.typography.titleMedium)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column {
                    ListItem(
                        headlineContent = { Text("Privacy Policy") },
                        modifier = Modifier.clickable {
                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW,
                                "https://example.com/privacy".toUri())
                            context.startActivity(intent)
                        }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text("Terms & Conditions") },
                        modifier = Modifier.clickable {
                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW,
                                "https://example.com/terms".toUri())
                            context.startActivity(intent)
                        }
                    )
                }
            }
            
            Text(
                text = "Version 1.0",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
