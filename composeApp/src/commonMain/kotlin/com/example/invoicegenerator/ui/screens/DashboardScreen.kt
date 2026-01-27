package com.example.invoicegenerator.ui.screens

import com.example.invoicegenerator.getPlatform

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import com.example.invoicegenerator.data.entity.Invoice
import com.example.invoicegenerator.ui.navigation.Screen
import com.example.invoicegenerator.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNewInvoice: () -> Unit,
    onNavigateTo: (String) -> Unit,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val stats by viewModel.stats.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Dashboard") })
        },
        bottomBar = {
            BottomNavigationBar(currentRoute = Screen.Dashboard.route, onNavigate = onNavigateTo)
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNewInvoice,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("New Invoice") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Sales Overview",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatsCard(
                        modifier = Modifier.weight(1f),
                        title = "This Month",
                        value = getPlatform().formatCurrency(stats.totalSales),
                        icon = Icons.Default.Email,
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatsCard(
                        modifier = Modifier.weight(1f),
                        title = "Paid",
                        value = "${stats.paidInvoicesCount}",
                        icon = Icons.Default.CheckCircle,
                        color = MaterialTheme.colorScheme.secondaryContainer
                    )
                    StatsCard(
                        modifier = Modifier.weight(1f),
                        title = "Unpaid",
                        value = "${stats.unpaidInvoicesCount}",
                        icon = Icons.Default.Person,
                        color = MaterialTheme.colorScheme.errorContainer
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Invoices",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { onNavigateTo(Screen.Invoices.route) }) {
                        Text("View All")
                    }
                }
            }

            if (stats.recentInvoices.isEmpty()) {
                item {
                    Text(
                        text = "No invoices created yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            } else {
                items(stats.recentInvoices) { invoice ->
                    InvoiceItemRow(invoice, onClick = { onNavigateTo(Screen.InvoicePreview.createRoute(invoice.id)) })
                }
            }
        }
    }
}

@Composable
fun StatsCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, style = MaterialTheme.typography.labelMedium)
            Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun InvoiceItemRow(invoice: Invoice, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "Inv #${invoice.invoiceNumber}", fontWeight = FontWeight.Bold)
                Text(text = getPlatform().formatCurrency(invoice.totalAmount), style = MaterialTheme.typography.bodyMedium)
            }
            if (invoice.isPaid) {
                SuggestionChip(
                    onClick = {},
                    label = { Text("Paid") },
                    colors = SuggestionChipDefaults.suggestionChipColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                )
            } else {
                SuggestionChip(
                    onClick = {},
                    label = { Text("Unpaid") },
                    colors = SuggestionChipDefaults.suggestionChipColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(currentRoute: String, onNavigate: (String) -> Unit) {
    NavigationBar {
        val isInvoicesSelected = currentRoute == Screen.Dashboard.route || currentRoute == Screen.Invoices.route
        NavigationBarItem(
            icon = { Icon(Icons.Default.Create, contentDescription = null) },
            label = { Text("Invoices") },
            selected = isInvoicesSelected,
            onClick = { onNavigate(Screen.Dashboard.route) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Customers") },
            selected = currentRoute == Screen.Customers.route,
            onClick = { onNavigate(Screen.Customers.route) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
            label = { Text("Items") },
            selected = currentRoute == Screen.Items.route,
            onClick = { onNavigate(Screen.Items.route) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            label = { Text("Settings") },
            selected = currentRoute == Screen.Settings.route,
            onClick = { onNavigate(Screen.Settings.route) }
        )
    }
}
