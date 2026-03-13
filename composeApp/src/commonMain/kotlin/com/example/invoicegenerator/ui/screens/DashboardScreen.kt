package com.example.invoicegenerator.ui.screens

import androidx.compose.foundation.background
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
import org.koin.compose.viewmodel.koinViewModel
import com.example.invoicegenerator.data.entity.Invoice
import com.example.invoicegenerator.Res
import com.example.invoicegenerator.*
import com.example.invoicegenerator.dashboard
import com.example.invoicegenerator.new_invoice
import com.example.invoicegenerator.sales_overview
import com.example.invoicegenerator.this_month
import com.example.invoicegenerator.paid
import com.example.invoicegenerator.unpaid
import com.example.invoicegenerator.recent_invoices
import com.example.invoicegenerator.view_all
import com.example.invoicegenerator.no_invoices
import com.example.invoicegenerator.invoices
import com.example.invoicegenerator.customers
import com.example.invoicegenerator.items
import com.example.invoicegenerator.settings
import com.example.invoicegenerator.inv_number_prefix
import com.example.invoicegenerator.ui.navigation.Screen
import com.example.invoicegenerator.viewmodel.DashboardViewModel
import org.jetbrains.compose.resources.stringResource
import com.example.invoicegenerator.viewmodel.SettingsViewModel
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNewInvoice: () -> Unit,
    onNavigateTo: (String) -> Unit,
    viewModel: DashboardViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val stats by viewModel.stats.collectAsState()
    val currency by settingsViewModel.currency.collectAsState(initial = "USD")

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(Res.string.dashboard)) })
        },
        bottomBar = {
            BottomNavigationBar(currentRoute = Screen.Dashboard.route, onNavigate = onNavigateTo)
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNewInvoice,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(stringResource(Res.string.new_invoice)) }
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
                    text = stringResource(Res.string.sales_overview),
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
                        title = stringResource(Res.string.this_month),
                        value = getPlatform().formatCurrency(stats.totalSales, currency),
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
                        title = stringResource(Res.string.paid),
                        value = "${stats.paidInvoicesCount}",
                        icon = Icons.Default.CheckCircle,
                        color = MaterialTheme.colorScheme.secondaryContainer
                    )
                    StatsCard(
                        modifier = Modifier.weight(1f),
                        title = stringResource(Res.string.unpaid),
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
                        text = stringResource(Res.string.recent_invoices),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { onNavigateTo(Screen.Invoices.route) }) {
                        Text(stringResource(Res.string.view_all))
                    }
                }
            }

            if (stats.recentInvoices.isEmpty()) {
                item {
                    Text(
                        text = stringResource(Res.string.no_invoices),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            } else {
                items(stats.recentInvoices) { invoice ->
                    InvoiceItemRow(
                        invoice,
                        currency,
                        onClick = { onNavigateTo(Screen.InvoicePreview.createRoute(invoice.id)) },
                        onStatusChange = { viewModel.toggleInvoiceStatus(invoice) }
                    )
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
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun InvoiceItemRow(
    invoice: Invoice,
    currency: String,
    onClick: () -> Unit = {},
    onStatusChange: () -> Unit = {},
    onDelete: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = 0.5f
            )
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "${stringResource(Res.string.inv_number_prefix)}${invoice.invoiceNumber}",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = getPlatform().formatCurrency(invoice.totalAmount, currency),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (invoice.isPaid) {
                SuggestionChip(
                    onClick = onStatusChange,
                    label = { Text(stringResource(Res.string.paid)) },
                    colors = SuggestionChipDefaults.suggestionChipColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    border = null
                )
            } else {
                SuggestionChip(
                    onClick = onStatusChange,
                    label = { Text(stringResource(Res.string.unpaid)) },
                    colors = SuggestionChipDefaults.suggestionChipColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    border = null
                )
            }
            if (onDelete != null) {
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete Invoice",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(currentRoute: String, onNavigate: (String) -> Unit) {
    NavigationBar {
        val isInvoicesSelected =
            currentRoute == Screen.Dashboard.route || currentRoute == Screen.Invoices.route
        NavigationBarItem(
            icon = { Icon(painterResource(Res.drawable.ic_invoice_bn), contentDescription = null) },
            label = { Text(stringResource(Res.string.invoices)) },
            selected = isInvoicesSelected,
            onClick = { onNavigate(Screen.Dashboard.route) }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painterResource(Res.drawable.ic_customer_bn),
                    contentDescription = null
                )
            },
            label = { Text(stringResource(Res.string.customers)) },
            selected = currentRoute == Screen.Customers.route,
            onClick = { onNavigate(Screen.Customers.route) }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(Res.drawable.ic_items), contentDescription = null) },
            label = { Text(stringResource(Res.string.items)) },
            selected = currentRoute == Screen.Items.route,
            onClick = { onNavigate(Screen.Items.route) }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(Res.drawable.ic_setting), contentDescription = null) },
            label = { Text(stringResource(Res.string.settings)) },
            selected = currentRoute == Screen.Settings.route,
            onClick = { onNavigate(Screen.Settings.route) }
        )
    }
}
