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

import org.jetbrains.compose.resources.stringResource
import com.example.invoicegenerator.Res
import com.example.invoicegenerator.*
import com.example.invoicegenerator.create_invoice
import com.example.invoicegenerator.select_customer
import com.example.invoicegenerator.add_item
import com.example.invoicegenerator.subtotal
import com.example.invoicegenerator.cgst
import com.example.invoicegenerator.sgst
import com.example.invoicegenerator.total_amount
import com.example.invoicegenerator.generate_invoice
import com.example.invoicegenerator.no_customers_found
import com.example.invoicegenerator.no_items_found
import com.example.invoicegenerator.cancel
import com.example.invoicegenerator.gstin
import com.example.invoicegenerator.select_item
import com.example.invoicegenerator.items
import com.example.invoicegenerator.invoice_prefix
import com.example.invoicegenerator.not_available
import com.example.invoicegenerator.tax_gst

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceCreateScreen(
    onPreview: (Long) -> Unit,
    onBack: () -> Unit,

    ) {

    val invoiceViewModel: InvoiceViewModel = koinViewModel()
    val businessViewModel: BusinessViewModel = koinViewModel()
    val settingsViewModel: SettingsViewModel = koinViewModel()
    val businessProfile by businessViewModel.businessProfile.collectAsState()
    val selectedCustomer by invoiceViewModel.selectedCustomer.collectAsState()
    val invoiceItems by invoiceViewModel.invoiceItems.collectAsState()
    val totals = invoiceViewModel.calculateTotals(businessProfile)
    val currency by settingsViewModel.currency.collectAsState(initial = "USD")

    val prefix = stringResource(Res.string.invoice_prefix)
    var invoiceNumber by remember(prefix) {
        mutableStateOf(
            "$prefix${
                kotlinx.datetime.Clock.System.now().toEpochMilliseconds() / 100000
            }"
        )
    }

    var showCustomerPicker by remember { mutableStateOf(false) }
    var showItemPicker by remember { mutableStateOf(false) }
    val customers by invoiceViewModel.customers.collectAsState()
    val availableItems by invoiceViewModel.items.collectAsState()

    if (showCustomerPicker) {
        AlertDialog(
            onDismissRequest = { showCustomerPicker = false },
            title = { Text(stringResource(Res.string.select_customer)) },
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
                        item { Text(stringResource(Res.string.no_customers_found)) }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCustomerPicker = false }) {
                    Text(stringResource(Res.string.cancel))
                }
            }
        )
    }

    if (showItemPicker) {
        AlertDialog(
            onDismissRequest = { showItemPicker = false },
            title = { Text(stringResource(Res.string.select_item)) },
            text = {
                LazyColumn {
                    items(availableItems.size) { index ->
                        val item = availableItems[index]
                        ListItem(
                            headlineContent = { Text(item.name) },
                            supportingContent = {
                                Text(
                                    getPlatform().formatCurrency(
                                        item.rate,
                                        currency
                                    )
                                )
                            },
                            modifier = Modifier.clickable {
                                invoiceViewModel.addInvoiceItem(item)
                                showItemPicker = false
                            }
                        )
                    }
                    if (availableItems.isEmpty()) {
                        item { Text(stringResource(Res.string.no_items_found)) }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showItemPicker = false }) {
                    Text(stringResource(Res.string.cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.create_invoice)) },
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
                            text = selectedCustomer?.name
                                ?: stringResource(Res.string.select_customer),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (selectedCustomer != null) {
                            Text(
                                text = "${stringResource(Res.string.gstin)}: ${
                                    selectedCustomer?.gstin ?: stringResource(
                                        Res.string.not_available
                                    )
                                }"
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Items List
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.items),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { showItemPicker = true }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text(stringResource(Res.string.add_item))
                }
            }

            LazyColumn(modifier = Modifier.weight(1f)) {
                itemsIndexed(invoiceItems) { index, item ->
                    ListItem(
                        headlineContent = { Text(item.itemName) },
                        supportingContent = {
                            Text(
                                "${item.quantity} x ${
                                    getPlatform().formatCurrency(
                                        item.rate,
                                        currency
                                    )
                                } (${item.gstRate}% ${stringResource(Res.string.tax_gst)})"
                            )
                        },
                        trailingContent = {
                            IconButton(onClick = { invoiceViewModel.removeItem(index) }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
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
                    TotalRow1(
                        stringResource(Res.string.subtotal),
                        platform.formatCurrency(totals.subTotal, currency)
                    )
                    TotalRow1(
                        stringResource(Res.string.cgst),
                        platform.formatCurrency(totals.cgst, currency)
                    )
                    TotalRow1(
                        stringResource(Res.string.sgst),
                        platform.formatCurrency(totals.sgst, currency)
                    )
                    TotalRow1(
                        stringResource(Res.string.total_amount),
                        platform.formatCurrency(totals.total, currency),
                        isBold = true
                    )
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
                Text(stringResource(Res.string.generate_invoice))
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
