package com.example.invoicegenerator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import com.example.invoicegenerator.ui.navigation.Screen
import com.example.invoicegenerator.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicesListScreen(
    onInvoiceClick: (Long) -> Unit,
    onNewInvoice: () -> Unit,
    onNavigateTo: (String) -> Unit,
    viewModel: DashboardViewModel = koinViewModel() // Reuse or create specific list VM
) {
    val stats by viewModel.stats.collectAsState()
    var filterByPaid by remember { mutableStateOf<Boolean?>(null) } // null = all, true = paid, false = unpaid

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Invoices") },
                navigationIcon = {
                    IconButton(onClick = { onNavigateTo(Screen.Dashboard.route) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Show filter dialog */ }) {
                        Icon(Icons.Default.Person, contentDescription = null)
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(currentRoute = Screen.Invoices.route, onNavigate = onNavigateTo)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNewInvoice) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { padding ->
        val filteredInvoices = when (filterByPaid) {
            true -> stats.allInvoices.filter { it.isPaid }
            false -> stats.allInvoices.filter { !it.isPaid }
            else -> stats.allInvoices
        }

        if (filteredInvoices.isEmpty()) {
            Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No invoices found")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredInvoices) { invoice ->
                    InvoiceItemRow(invoice, onClick = { onInvoiceClick(invoice.id) })
                }
            }
        }
    }
}
