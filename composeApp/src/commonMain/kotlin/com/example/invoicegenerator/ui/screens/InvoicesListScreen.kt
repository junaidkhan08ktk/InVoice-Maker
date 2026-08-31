package com.example.invoicegenerator.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.invoicegenerator.Res
import com.example.invoicegenerator.*
import com.example.invoicegenerator.invoices
import com.example.invoicegenerator.ui.components.*
import com.example.invoicegenerator.ui.navigation.Screen
import com.example.invoicegenerator.ui.theme.*
import com.example.invoicegenerator.viewmodel.DashboardViewModel
import com.example.invoicegenerator.viewmodel.InvoiceWithCustomer
import com.example.invoicegenerator.viewmodel.SettingsViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicesListScreen(
    onInvoiceClick: (Long) -> Unit,
    onNewInvoice: () -> Unit,
    onNavigateTo: (String) -> Unit,
    viewModel: DashboardViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val stats by viewModel.stats.collectAsState()
    val currency by settingsViewModel.currency.collectAsState(initial = "USD")

    var searchQuery by remember { mutableStateOf("") }
    var filterOption by remember { mutableStateOf("All") } // "All", "Paid", "Unpaid"
    var showFilterDialog by remember { mutableStateOf(false) }

    val filteredInvoices = stats.allInvoices.filter { itemData ->
        val inv = itemData.invoice
        val custName = itemData.customer?.name ?: ""

        val matchesQuery = searchQuery.isBlank() ||
                inv.invoiceNumber.contains(searchQuery, ignoreCase = true) ||
                custName.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (filterOption) {
            "Paid" -> inv.isPaid
            "Unpaid" -> !inv.isPaid
            else -> true
        }

        matchesQuery && matchesFilter
    }

    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            containerColor = SurfaceCard,
            shape = RoundedCornerShape(20.dp),
            title = { Text("Filter Invoices", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("All", "Paid", "Unpaid").forEach { opt ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    filterOption = opt
                                    showFilterDialog = false
                                }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = filterOption == opt,
                                onClick = {
                                    filterOption = opt
                                    showFilterDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryPurple)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(opt, style = TextStyle(fontSize = 15.sp, color = TextPrimary))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFilterDialog = false }) {
                    Text("Close", color = PrimaryPurple, fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }


    Scaffold(
        containerColor = ScreenBackground,
        bottomBar = {
            AppBottomNavigationBar(currentRoute = Screen.Invoices.route, onNavigate = onNavigateTo)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewInvoice,
                shape = CircleShape,
                containerColor = PrimaryPurple,
                contentColor = Color.White,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Invoice", modifier = Modifier.size(28.dp))
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                AppScreenHeader(
                    title = stringResource(Res.string.invoices),
                    subtitle = "Manage and track all customer invoices."
                )
            }

            item {
                SearchBarWithFilter(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Search invoices or clients...",
                    onFilterClick = { showFilterDialog = true },
                    isFilterActive = filterOption != "All"
                )
            }

            if (filteredInvoices.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = SurfaceCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (searchQuery.isNotEmpty()) "No matching invoices found" else "No invoices created yet",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = TextSecondary
                                )
                            )
                        }
                    }
                }
            } else {
                items(filteredInvoices) { itemData ->
                    DashboardInvoiceCard(
                        invoiceData = itemData,
                        currency = currency,
                        onClick = { onInvoiceClick(itemData.invoice.id) },
                        onToggleStatus = { viewModel.toggleInvoiceStatus(itemData.invoice) }
                    )
                }
            }
        }
    }
}

