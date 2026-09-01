package com.example.invoicegenerator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.invoicegenerator.add_customer
import com.example.invoicegenerator.address
import com.example.invoicegenerator.cancel
import com.example.invoicegenerator.customers
import com.example.invoicegenerator.data.entity.Customer
import com.example.invoicegenerator.getPlatform
import com.example.invoicegenerator.gstin
import com.example.invoicegenerator.name
import com.example.invoicegenerator.save
import com.example.invoicegenerator.ui.components.*
import com.example.invoicegenerator.ui.navigation.Screen
import com.example.invoicegenerator.ui.theme.*
import com.example.invoicegenerator.viewmodel.InvoiceViewModel
import com.example.invoicegenerator.viewmodel.SettingsViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomersScreen(
    onNavigateTo: (String) -> Unit,
    viewModel: InvoiceViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val customers by viewModel.customers.collectAsState()
    val allInvoices by viewModel.allInvoices.collectAsState()
    val currency by settingsViewModel.currency.collectAsState(initial = "USD")
    val platform = getPlatform()

    // Search and filter state
    var searchQuery by remember { mutableStateOf("") }
    var filterOption by remember { mutableStateOf("All") } // "All", "Active", "Inactive", "Has Unpaid"
    var showFilterDialog by remember { mutableStateOf(false) }

    // Add / Edit Dialog state
    var showCustomerDialog by remember { mutableStateOf(false) }
    var editingCustomer by remember { mutableStateOf<Customer?>(null) }
    var nameInput by remember { mutableStateOf("") }
    var gstinInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var addressInput by remember { mutableStateOf("") }

    // Delete confirmation dialog
    var customerToDelete by remember { mutableStateOf<Customer?>(null) }

    fun openAddDialog() {
        editingCustomer = null
        nameInput = ""
        gstinInput = ""
        phoneInput = ""
        emailInput = ""
        addressInput = ""
        showCustomerDialog = true
    }

    fun openEditDialog(cust: Customer) {
        editingCustomer = cust
        nameInput = cust.name
        gstinInput = cust.gstin ?: ""
        phoneInput = cust.phone ?: ""
        emailInput = cust.email ?: ""
        addressInput = cust.address ?: ""
        showCustomerDialog = true
    }

    // Customer Stats Calculation
    val customerInvoiceMap = remember(allInvoices) {
        allInvoices.groupBy { it.customerId }
    }
    val customerSalesMap = remember(allInvoices) {
        allInvoices.groupBy { it.customerId }
            .mapValues { entry -> entry.value.sumOf { it.totalAmount } }
    }
    val customerUnpaidMap = remember(allInvoices) {
        allInvoices.groupBy { it.customerId }
            .mapValues { entry -> entry.value.any { !it.isPaid } }
    }

    val totalCustomers = customers.size
    val activeCustomers = customers.count { customerInvoiceMap[it.id]?.isNotEmpty() == true }
    val inactiveCustomers = totalCustomers - activeCustomers
    val activePercentage = if (totalCustomers > 0) (activeCustomers * 100) / totalCustomers else 100
    val inactivePercentage = if (totalCustomers > 0) (inactiveCustomers * 100) / totalCustomers else 0
    val totalSales = customerSalesMap.values.sum()

    // Filter and Search list
    val filteredCustomers = customers.filter { cust ->
        val matchesQuery = searchQuery.isBlank() ||
                cust.name.contains(searchQuery, ignoreCase = true) ||
                (cust.gstin?.contains(searchQuery, ignoreCase = true) == true) ||
                (cust.phone?.contains(searchQuery, ignoreCase = true) == true) ||
                (cust.email?.contains(searchQuery, ignoreCase = true) == true)

        val matchesFilter = when (filterOption) {
            "Active" -> customerInvoiceMap[cust.id]?.isNotEmpty() == true
            "Inactive" -> customerInvoiceMap[cust.id].isNullOrEmpty()
            "Has Unpaid" -> customerUnpaidMap[cust.id] == true
            else -> true
        }

        matchesQuery && matchesFilter
    }

    // Add / Edit Modal Dialog
    if (showCustomerDialog) {
        AlertDialog(
            onDismissRequest = { showCustomerDialog = false },
            containerColor = SurfaceCard,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = if (editingCustomer == null) stringResource(Res.string.add_customer) else "Edit Customer",
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextPrimary)
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val tfColors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryPurple,
                        focusedLabelColor = PrimaryPurple,
                        unfocusedBorderColor = SurfaceInputBorder,
                        focusedContainerColor = SurfaceInput,
                        unfocusedContainerColor = SurfaceInput
                    )

                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Customer Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = tfColors
                    )
                    OutlinedTextField(
                        value = gstinInput,
                        onValueChange = { gstinInput = it },
                        label = { Text("GSTIN") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = tfColors
                    )
                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text("Phone Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = tfColors
                    )
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Email Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = tfColors
                    )
                    OutlinedTextField(
                        value = addressInput,
                        onValueChange = { addressInput = it },
                        label = { Text("Address") },
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = tfColors
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nameInput.isNotBlank()) {
                            if (editingCustomer != null) {
                                viewModel.updateCustomer(
                                    editingCustomer!!.copy(
                                        name = nameInput.trim(),
                                        gstin = gstinInput.trim().ifEmpty { null },
                                        phone = phoneInput.trim().ifEmpty { null },
                                        email = emailInput.trim().ifEmpty { null },
                                        address = addressInput.trim().ifEmpty { null }
                                    )
                                )
                            } else {
                                viewModel.addCustomer(
                                    name = nameInput.trim(),
                                    gstin = gstinInput.trim().ifEmpty { null },
                                    phone = phoneInput.trim().ifEmpty { null },
                                    email = emailInput.trim().ifEmpty { null },
                                    address = addressInput.trim().ifEmpty { null }
                                )
                            }
                            showCustomerDialog = false
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                ) {
                    Text(stringResource(Res.string.save), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomerDialog = false }) {
                    Text(stringResource(Res.string.cancel), color = TextSecondary)
                }
            }
        )
    }

    // Filter Dialog
    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            containerColor = SurfaceCard,
            shape = RoundedCornerShape(20.dp),
            title = { Text("Filter Customers", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("All", "Active", "Inactive", "Has Unpaid").forEach { opt ->
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

    // Delete Confirmation Dialog
    customerToDelete?.let { cust ->
        AlertDialog(
            onDismissRequest = { customerToDelete = null },
            containerColor = SurfaceCard,
            shape = RoundedCornerShape(20.dp),
            title = { Text("Delete Customer", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = { Text("Are you sure you want to delete ${cust.name}?", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCustomer(cust)
                        customerToDelete = null
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StatRed)
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { customerToDelete = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    Scaffold(
        containerColor = ScreenBackground,
        bottomBar = {
            AppBottomNavigationBar(
                currentRoute = Screen.Customers.route,
                onNavigate = onNavigateTo
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { openAddDialog() },
                shape = CircleShape,
                containerColor = PrimaryPurple,
                contentColor = Color.White,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Customer",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Header with top right action icon
            item {
                AppScreenHeader(
                    title = "Customers",
                    subtitle = "Manage your all customers in one place.",
                    trailingContent = {

                    }
                )
            }

            // 2. Stat Cards Row (4 cards)
            item {
                val statCards = listOf(
                    StatCardData(
                        title = "Total Customers",
                        value = "$totalCustomers",
                        subValue = "All time",
                        icon = Icons.Default.People,
                        iconBgColor = StatPurpleLight,
                        iconTintColor = StatPurple,
                        subValueColor = StatPurple
                    ),
                    StatCardData(
                        title = "Active Customers",
                        value = "$activeCustomers",
                        subValue = "$activePercentage%",
                        icon = Icons.Default.CheckCircle,
                        iconBgColor = StatGreenLight,
                        iconTintColor = StatGreen,
                        subValueColor = StatGreen
                    ),
                    StatCardData(
                        title = "Inactive Customers",
                        value = "$inactiveCustomers",
                        subValue = "$inactivePercentage%",
                        icon = Icons.Default.AccessTime,
                        iconBgColor = StatOrangeLight,
                        iconTintColor = StatOrange,
                        subValueColor = StatOrange
                    ),
                    StatCardData(
                        title = "Total Sales",
                        value = platform.formatCurrency(totalSales, currency),
                        subValue = "This Month",
                        icon = Icons.Default.ReceiptLong,
                        iconBgColor = StatBlueLight,
                        iconTintColor = StatBlue,
                        subValueColor = StatBlue
                    )
                )
                StatCardsRow(cards = statCards)
            }

            // 3. Search & Filter Bar
            item {
                SearchBarWithFilter(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Search customers...",
                    onFilterClick = { showFilterDialog = true },
                    isFilterActive = filterOption != "All"
                )
            }

            // 4. Customer List
            if (filteredCustomers.isEmpty()) {
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
                                text = if (searchQuery.isNotEmpty()) "No matching customers found" else "No customers added yet. Tap + to add one.",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = TextSecondary
                                )
                            )
                        }
                    }
                }
            } else {
                items(filteredCustomers) { customer ->
                    val custInvoices = customerInvoiceMap[customer.id] ?: emptyList()
                    val custSales = customerSalesMap[customer.id] ?: 0.0
                    val hasUnpaid = customerUnpaidMap[customer.id] == true

                    CustomerCardItem(
                        customer = customer,
                        invoiceCount = custInvoices.size,
                        totalSales = custSales,
                        hasUnpaid = hasUnpaid,
                        currency = currency,
                        onEdit = { openEditDialog(customer) },
                        onDelete = { customerToDelete = customer }
                    )
                }
            }
        }
    }
}

@Composable
fun CustomerCardItem(
    customer: Customer,
    invoiceCount: Int,
    totalSales: Double,
    hasUnpaid: Boolean,
    currency: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val platform = getPlatform()
    val initial = customer.name.firstOrNull()?.uppercase() ?: "C"
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Avatar + Details
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Circular Avatar
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(PrimaryPurpleLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initial,
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryPurple
                        )
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = customer.name,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )

                    Text(
                        text = "GSTIN: ${customer.gstin ?: "—"}",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    )

                    // Email Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email",
                            tint = TextMuted,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = customer.email ?: "—",
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = TextMuted
                            ),
                            maxLines = 1
                        )
                    }

                    // Phone Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Phone",
                            tint = TextMuted,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = customer.phone ?: "—",
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = TextMuted
                            ),
                            maxLines = 1
                        )
                    }
                }
            }

            // Right: Invoice stats + Status badge + Actions (3 dots & Trash)
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Top row with 3 dots menu
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (invoiceCount == 1) "1 Invoice" else "$invoiceCount Invoices",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = PrimaryPurple
                        )
                    )

                    Box {
                        IconButton(
                            onClick = { menuExpanded = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit Customer") },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                                onClick = {
                                    menuExpanded = false
                                    onEdit()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete", color = StatRed) },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = StatRed) },
                                onClick = {
                                    menuExpanded = false
                                    onDelete()
                                }
                            )
                        }
                    }
                }

                // Total Sales Amount
                Text(
                    text = platform.formatCurrency(totalSales, currency),
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )

                // Status Badge & Red Delete Trash Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (invoiceCount > 0) {
                        StatusBadge(
                            status = if (hasUnpaid) BadgeStatus.UNPAID else BadgeStatus.PAID
                        )
                    } else {
                        StatusBadge(
                            status = BadgeStatus.INACTIVE,
                            customText = "No Invoices"
                        )
                    }

                    // Red Trash Button
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(StatRedLight)
                            .clickable { onDelete() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = StatRed,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

