package com.example.invoicegenerator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import com.example.invoicegenerator.data.entity.Item
import com.example.invoicegenerator.getPlatform
import com.example.invoicegenerator.ui.navigation.Screen
import com.example.invoicegenerator.viewmodel.InvoiceViewModel
import com.example.invoicegenerator.viewmodel.SettingsViewModel
import org.jetbrains.compose.resources.stringResource
import com.example.invoicegenerator.generated.resources.Res
import com.example.invoicegenerator.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsScreen(
    onNavigateTo: (String) -> Unit,
    viewModel: InvoiceViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val items by viewModel.items.collectAsState()
    val currency by settingsViewModel.currency.collectAsState(initial = "USD")

    var showAddDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var rate by remember { mutableStateOf("") }
    var gstRate by remember { mutableStateOf("18") }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Item") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Item Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = rate, onValueChange = { rate = it }, label = { Text("Rate") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = gstRate, onValueChange = { gstRate = it }, label = { Text(stringResource(Res.string.tax_rate)) }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (name.isNotBlank() && rate.toDoubleOrNull() != null) {
                        viewModel.addItem(name, rate.toDouble(), gstRate.toDoubleOrNull() ?: 18.0)
                        showAddDialog = false
                        name = ""; rate = ""; gstRate = "18"
                    }
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Cancel") } }
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(Res.string.items)) }) },
        bottomBar = { BottomNavigationBar(currentRoute = Screen.Items.route, onNavigate = onNavigateTo) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { padding ->
        if (items.isEmpty()) {
            Box(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No items yet. Tap + to add one.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items) { item ->
                    ItemRow(item, currency)
                }
            }
        }
    }
}

@Composable
fun ItemRow(item: Item, currency: String) {
    val platform = getPlatform()
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = item.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Rate: ${platform.formatCurrency(item.rate, currency)} | GST: ${item.gstRate}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
