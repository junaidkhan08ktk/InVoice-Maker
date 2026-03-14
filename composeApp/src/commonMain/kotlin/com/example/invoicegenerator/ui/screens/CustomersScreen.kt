package com.example.invoicegenerator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import com.example.invoicegenerator.data.entity.Customer
import com.example.invoicegenerator.Res
import com.example.invoicegenerator.*
import com.example.invoicegenerator.customers
import com.example.invoicegenerator.gstin
import com.example.invoicegenerator.ui.navigation.Screen
import com.example.invoicegenerator.viewmodel.InvoiceViewModel
import org.jetbrains.compose.resources.stringResource
import com.example.invoicegenerator.add_customer
import com.example.invoicegenerator.name
import com.example.invoicegenerator.address
import com.example.invoicegenerator.save
import com.example.invoicegenerator.cancel
import com.example.invoicegenerator.not_available
import androidx.compose.material.icons.filled.Delete

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomersScreen(
    onNavigateTo: (String) -> Unit,
) {
    val viewModel: InvoiceViewModel = koinViewModel()

    val customers by viewModel.customers.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var gstin by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(stringResource(Res.string.add_customer)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(stringResource(Res.string.name)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = gstin,
                        onValueChange = { gstin = it },
                        label = { Text(stringResource(Res.string.gstin)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text(stringResource(Res.string.address)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (name.isNotBlank()) {
                        viewModel.addCustomer(name, gstin, address)
                        showAddDialog = false
                        name = ""; gstin = ""; address = ""
                    }
                }) { Text(stringResource(Res.string.save)) }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text(stringResource(Res.string.cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(Res.string.customers)) }) },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = Screen.Customers.route,
                onNavigate = onNavigateTo
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(customers) { customer ->
                CustomerRow(customer, onDelete = { viewModel.deleteCustomer(customer) })
            }
        }
    }
}

@Composable
fun CustomerRow(customer: Customer, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Person, contentDescription = null)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = customer.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${stringResource(Res.string.gstin)}: ${
                        customer.gstin ?: stringResource(
                            Res.string.not_available
                        )
                    }", style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
