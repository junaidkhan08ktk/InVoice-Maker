package com.example.invoicegenerator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.invoicegenerator.data.entity.Item
import com.example.invoicegenerator.ui.navigation.Screen
import com.example.invoicegenerator.viewmodel.InvoiceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsScreen(
    onNavigateTo: (String) -> Unit,
    viewModel: InvoiceViewModel = hiltViewModel()
) {
    val items by viewModel.items.collectAsState()

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
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Item Name") })
                    OutlinedTextField(value = rate, onValueChange = { rate = it }, label = { Text("Rate") })
                    OutlinedTextField(value = gstRate, onValueChange = { gstRate = it }, label = { Text("GST Rate (%)") })
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
        topBar = { TopAppBar(title = { Text("Items") }) },
        bottomBar = { BottomNavigationBar(currentRoute = Screen.Items.route, onNavigate = onNavigateTo) },
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
            items(items) { item ->
                ItemRow(item)
            }
        }
    }
}

@Composable
fun ItemRow(item: Item) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = item.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "Rate: ₹${item.rate} | GST: ${item.gstRate}%", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
