package com.example.invoicegenerator.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.invoicegenerator.create_invoice
import com.example.invoicegenerator.getPlatform
import com.example.invoicegenerator.ui.components.SearchBarWithFilter
import com.example.invoicegenerator.ui.theme.*
import com.example.invoicegenerator.viewmodel.BusinessViewModel
import com.example.invoicegenerator.viewmodel.InvoiceViewModel
import com.example.invoicegenerator.viewmodel.SettingsViewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceCreateScreen(
    onPreview: (Long) -> Unit,
    onBack: () -> Unit
) {
    val invoiceViewModel: InvoiceViewModel = koinViewModel()
    val businessViewModel: BusinessViewModel = koinViewModel()
    val settingsViewModel: SettingsViewModel = koinViewModel()
    val businessProfile by businessViewModel.businessProfile.collectAsState()
    val selectedCustomer by invoiceViewModel.selectedCustomer.collectAsState()
    val invoiceItems by invoiceViewModel.invoiceItems.collectAsState()
    val totals = invoiceViewModel.calculateTotals(businessProfile)
    val currency by settingsViewModel.currency.collectAsState(initial = "USD")
    val platform = getPlatform()

    val prefix = stringResource(Res.string.invoice_prefix)
    var invoiceNumber by remember(prefix) {
        mutableStateOf("$prefix${Clock.System.now().toEpochMilliseconds() / 100000}")
    }

    var showCustomerPicker by remember { mutableStateOf(false) }
    var showItemPicker by remember { mutableStateOf(false) }
    val customers by invoiceViewModel.customers.collectAsState()
    val availableItems by invoiceViewModel.items.collectAsState()

    // Customer Picker Dialog
    if (showCustomerPicker) {
        var custSearchQuery by remember { mutableStateOf("") }
        val filteredCusts = customers.filter {
            custSearchQuery.isBlank() || it.name.contains(custSearchQuery, ignoreCase = true) ||
                    (it.gstin?.contains(custSearchQuery, ignoreCase = true) == true)
        }

        AlertDialog(
            onDismissRequest = { showCustomerPicker = false },
            containerColor = SurfaceCard,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = stringResource(Res.string.select_customer),
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = custSearchQuery,
                        onValueChange = { custSearchQuery = it },
                        placeholder = { Text("Search customer name...") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryPurple,
                            unfocusedBorderColor = SurfaceInputBorder,
                            focusedContainerColor = SurfaceInput,
                            unfocusedContainerColor = SurfaceInput
                        )
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (filteredCusts.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        stringResource(Res.string.no_customers_found),
                                        style = TextStyle(fontSize = 14.sp, color = TextSecondary)
                                    )
                                }
                            }
                        } else {
                            items(filteredCusts.size) { index ->
                                val cust = filteredCusts[index]
                                val initial = cust.name.firstOrNull()?.uppercase() ?: "C"

                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            invoiceViewModel.selectCustomer(cust)
                                            showCustomerPicker = false
                                        },
                                    color = if (selectedCustomer?.id == cust.id) PrimaryPurpleLight else SurfaceInput,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(
                                        1.dp,
                                        if (selectedCustomer?.id == cust.id) PrimaryPurple else SurfaceInputBorder
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(PrimaryPurpleLight),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = initial,
                                                style = TextStyle(fontWeight = FontWeight.Bold, color = PrimaryPurple, fontSize = 16.sp)
                                            )
                                        }

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = cust.name,
                                                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                            )
                                            Text(
                                                text = "GSTIN: ${cust.gstin ?: "—"}",
                                                style = TextStyle(fontSize = 12.sp, color = TextSecondary)
                                            )
                                        }

                                        if (selectedCustomer?.id == cust.id) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = PrimaryPurple)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCustomerPicker = false }) {
                    Text("Close", color = PrimaryPurple, fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }

    // Item Picker Dialog
    if (showItemPicker) {
        var itemSearchQuery by remember { mutableStateOf("") }
        val filteredItemsList = availableItems.filter {
            itemSearchQuery.isBlank() || it.name.contains(itemSearchQuery, ignoreCase = true)
        }

        AlertDialog(
            onDismissRequest = { showItemPicker = false },
            containerColor = SurfaceCard,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = stringResource(Res.string.select_item),
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = itemSearchQuery,
                        onValueChange = { itemSearchQuery = it },
                        placeholder = { Text("Search item name...") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryPurple,
                            unfocusedBorderColor = SurfaceInputBorder,
                            focusedContainerColor = SurfaceInput,
                            unfocusedContainerColor = SurfaceInput
                        )
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (filteredItemsList.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        stringResource(Res.string.no_items_found),
                                        style = TextStyle(fontSize = 14.sp, color = TextSecondary)
                                    )
                                }
                            }
                        } else {
                            items(filteredItemsList.size) { index ->
                                val item = filteredItemsList[index]

                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            invoiceViewModel.addInvoiceItem(item)
                                            showItemPicker = false
                                        },
                                    color = SurfaceInput,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, SurfaceInputBorder)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(38.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(PrimaryPurpleLight),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    Icons.Default.ShoppingCart,
                                                    contentDescription = null,
                                                    tint = PrimaryPurple,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }

                                            Column {
                                                Text(
                                                    text = item.name,
                                                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                                )
                                                Text(
                                                    text = "GST: ${item.gstRate}% • Unit: ${item.unit}",
                                                    style = TextStyle(fontSize = 12.sp, color = TextSecondary)
                                                )
                                            }
                                        }

                                        Text(
                                            text = platform.formatCurrency(item.rate, currency),
                                            style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryPurple)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showItemPicker = false }) {
                    Text("Close", color = PrimaryPurple, fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }

    Scaffold(
        containerColor = ScreenBackground,
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SurfaceCard,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, SurfaceCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PrimaryPurpleLight)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PrimaryPurple,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = stringResource(Res.string.create_invoice),
                            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                        )
                        Text(
                            text = "Add client & items to generate invoice",
                            style = TextStyle(fontSize = 12.sp, color = TextSecondary)
                        )
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SurfaceCard,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, SurfaceCardBorder)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
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
                            .height(52.dp),
                        enabled = selectedCustomer != null && invoiceItems.isNotEmpty(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryPurple,
                            disabledContainerColor = Color(0xFFE2E8F0)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = stringResource(Res.string.generate_invoice),
                                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            )
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Invoice Number & Date
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, SurfaceCardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Invoice Details",
                            style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        )

                        OutlinedTextField(
                            value = invoiceNumber,
                            onValueChange = { invoiceNumber = it },
                            label = { Text("Invoice Number") },
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Tag, contentDescription = null, tint = PrimaryPurple) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryPurple,
                                unfocusedBorderColor = SurfaceInputBorder,
                                focusedContainerColor = SurfaceInput,
                                unfocusedContainerColor = SurfaceInput
                            )
                        )
                    }
                }
            }

            // 2. Customer Selection Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { showCustomerPicker = true },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, SurfaceCardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Client Information",
                                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            )
                            Text(
                                text = if (selectedCustomer == null) "+ Select" else "Change",
                                style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PrimaryPurple)
                            )
                        }

                        if (selectedCustomer != null) {
                            val cust = selectedCustomer!!
                            val initial = cust.name.firstOrNull()?.uppercase() ?: "C"

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryPurpleLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = initial,
                                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryPurple)
                                    )
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = cust.name,
                                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    )
                                    Text(
                                        text = "GSTIN: ${cust.gstin ?: "—"}",
                                        style = TextStyle(fontSize = 12.sp, color = TextSecondary)
                                    )
                                    if (!cust.phone.isNullOrBlank()) {
                                        Text(
                                            text = "Phone: ${cust.phone}",
                                            style = TextStyle(fontSize = 12.sp, color = TextMuted)
                                        )
                                    }
                                }
                            }
                        } else {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = SurfaceInput,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, SurfaceInputBorder)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PersonAdd,
                                        contentDescription = null,
                                        tint = PrimaryPurple,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Tap to choose a customer",
                                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = PrimaryPurple)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Invoice Items Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.items),
                            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        )
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(PrimaryPurpleLight)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${invoiceItems.size}",
                                style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryPurple)
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { showItemPicker = true },
                        color = PrimaryPurpleLight
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = PrimaryPurple, modifier = Modifier.size(16.dp))
                            Text(
                                text = stringResource(Res.string.add_item),
                                style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryPurple)
                            )
                        }
                    }
                }
            }

            // 4. Added Items List
            if (invoiceItems.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        border = BorderStroke(1.dp, SurfaceCardBorder)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No items added yet. Tap '+ Add Item' above.",
                                style = TextStyle(fontSize = 14.sp, color = TextSecondary)
                            )
                        }
                    }
                }
            } else {
                itemsIndexed(invoiceItems) { index, item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        border = BorderStroke(1.dp, SurfaceCardBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Left Details
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text(
                                    text = item.itemName,
                                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                )
                                Text(
                                    text = "${platform.formatCurrency(item.rate, currency)} • Tax: ${item.gstRate}%",
                                    style = TextStyle(fontSize = 12.sp, color = TextSecondary)
                                )
                                Text(
                                    text = "Qty: ${item.quantity}  =  ${platform.formatCurrency(item.rate * item.quantity, currency)}",
                                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PrimaryPurple)
                                )
                            }

                            // Delete button
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(StatRedLight)
                                    .clickable { invoiceViewModel.removeItem(index) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove Item",
                                    tint = StatRed,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 5. Invoice Totals Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, SurfaceCardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Payment Summary",
                            style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        )

                        ModernTotalRow(
                            label = stringResource(Res.string.subtotal),
                            value = platform.formatCurrency(totals.subTotal, currency)
                        )
                        ModernTotalRow(
                            label = "${stringResource(Res.string.cgst)} (Central GST)",
                            value = platform.formatCurrency(totals.cgst, currency)
                        )
                        ModernTotalRow(
                            label = "${stringResource(Res.string.sgst)} (State GST)",
                            value = platform.formatCurrency(totals.sgst, currency)
                        )

                        HorizontalDivider(color = SurfaceCardBorder, modifier = Modifier.padding(vertical = 4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(Res.string.total_amount),
                                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            )
                            Text(
                                text = platform.formatCurrency(totals.total, currency),
                                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryPurple)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModernTotalRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = TextStyle(fontSize = 13.sp, color = TextSecondary))
        Text(text = value, style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary))
    }
}

