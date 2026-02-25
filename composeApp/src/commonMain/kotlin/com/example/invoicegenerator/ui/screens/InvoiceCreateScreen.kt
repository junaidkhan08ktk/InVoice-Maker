package com.example.invoicegenerator.ui.screens

import com.example.invoicegenerator.getPlatform

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import com.example.invoicegenerator.data.entity.Business
import com.example.invoicegenerator.viewmodel.BusinessViewModel
import com.example.invoicegenerator.viewmodel.InvoiceViewModel
import com.example.invoicegenerator.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceCreateScreen(
    onPreview: (Long) -> Unit,
    onBack: () -> Unit,
    invoiceViewModel: InvoiceViewModel = koinViewModel(),
    businessViewModel: BusinessViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val businessProfile by businessViewModel.businessProfile.collectAsState()
    val selectedCustomer by invoiceViewModel.selectedCustomer.collectAsState()
    val invoiceItems by invoiceViewModel.invoiceItems.collectAsState()
    val totals = invoiceViewModel.calculateTotals(businessProfile)
    val currency by settingsViewModel.currency.collectAsState(initial = "USD")

    var invoiceNumber by remember { mutableStateOf("INV-${kotlinx.datetime.Clock.System.now().toEpochMilliseconds() / 100000}") }

    var showCustomerPicker by remember { mutableStateOf(false) }
    var showItemPicker by remember { mutableStateOf(false) }
    val customers by invoiceViewModel.customers.collectAsState()
    val availableItems by invoiceViewModel.items.collectAsState()

    if (showCustomerPicker) {
        AlertDialog(
            onDismissRequest = { showCustomerPicker = false },
            title = { Text("Select Customer") },
            text = {
                LazyColumn {
                    items(customers.size) { index ->
                        val customer = customers[index]
                        ListItem(
                            headlineContent = { Text(customer.name) },
                            modifier = Modifier.clickable {
                                invoiceViewModel.selectCustomer(customer)
                                showCustomerPicker = false
                            }
                        )
                    }
                    if (customers.isEmpty()) {
                        item { Text("No customers found. Add one in the Customers tab.") }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showCustomerPicker = false }) { Text("Cancel") } }
        )
    }

    if (showItemPicker) {
        AlertDialog(
            onDismissRequest = { showItemPicker = false },
            title = { Text("Select Item") },
            text = {
                LazyColumn {
                    items(availableItems.size) { index ->
                        val item = availableItems[index]
                        ListItem(
                            headlineContent = { Text(item.name) },
                            supportingContent = { Text(getPlatform().formatCurrency(item.rate, currency)) },
                            modifier = Modifier.clickable {
                                invoiceViewModel.addInvoiceItem(item)
                                showItemPicker = false
                            }
                        )
                    }
                    if (availableItems.isEmpty()) {
                        item { Text("No items found. Add one in the Items tab.") }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showItemPicker = false }) { Text("Cancel") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Invoice") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Customer Selection
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showCustomerPicker = true }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Person, contentDescription = null)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = selectedCustomer?.name ?: "Select Customer",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (selectedCustomer != null) {
                            Text(text = "GSTIN: ${selectedCustomer?.gstin ?: "N/A"}")
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.Person, contentDescription = null)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Items List
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Items", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                TextButton(onClick = { showItemPicker = true }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text("Add Item")
                }
            }

            LazyColumn(modifier = Modifier.weight(1f)) {
                itemsIndexed(invoiceItems) { index, item ->
                    ListItem(
                        headlineContent = { Text(item.itemName) },
                        supportingContent = { Text("${item.quantity} x ${getPlatform().formatCurrency(item.rate, currency)} (${item.gstRate}% GST)") },
                        trailingContent = {
                            IconButton(onClick = { invoiceViewModel.removeItem(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    )
                    HorizontalDivider()
                }
            }

            // Totals
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val platform = getPlatform()
                    TotalRow1("Subtotal", platform.formatCurrency(totals.subTotal, currency))
                    TotalRow1("CGST", platform.formatCurrency(totals.cgst, currency))
                    TotalRow1("SGST", platform.formatCurrency(totals.sgst, currency))
                    TotalRow1("Total Amount", platform.formatCurrency(totals.total, currency), isBold = true)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    businessProfile?.let {
                        invoiceViewModel.saveInvoice(it.id, invoiceNumber) { id ->
                            onPreview(id)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = selectedCustomer != null && invoiceItems.isNotEmpty()
            ) {
                Text("Generate Invoice")
            }
        }
    }
}

@Composable
fun TotalRow1(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
        Text(text = value, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
    }
}
