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
import com.example.invoicegenerator.add_item
import com.example.invoicegenerator.cancel
import com.example.invoicegenerator.data.entity.Item
import com.example.invoicegenerator.getPlatform
import com.example.invoicegenerator.item_name
import com.example.invoicegenerator.items
import com.example.invoicegenerator.rate
import com.example.invoicegenerator.save
import com.example.invoicegenerator.tax_gst
import com.example.invoicegenerator.tax_rate
import com.example.invoicegenerator.ui.components.*
import com.example.invoicegenerator.ui.navigation.Screen
import com.example.invoicegenerator.ui.theme.*
import com.example.invoicegenerator.viewmodel.InvoiceViewModel
import com.example.invoicegenerator.viewmodel.SettingsViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsScreen(
    onNavigateTo: (String) -> Unit,
    viewModel: InvoiceViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val items by viewModel.items.collectAsState()
    val currency by settingsViewModel.currency.collectAsState(initial = "USD")
    val platform = getPlatform()

    // Search and filter state
    var searchQuery by remember { mutableStateOf("") }
    var filterOption by remember { mutableStateOf("All") } // "All", "GST 18%", "GST 12%", "GST 5%", "GST 0%"
    var showFilterDialog by remember { mutableStateOf(false) }

    // Add / Edit Dialog state
    var showItemDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<Item?>(null) }
    var nameInput by remember { mutableStateOf("") }
    var rateInput by remember { mutableStateOf("") }
    var gstRateInput by remember { mutableStateOf("18.0") }
    var unitInput by remember { mutableStateOf("Nos") }
    var descriptionInput by remember { mutableStateOf("") }

    // Delete confirmation dialog
    var itemToDelete by remember { mutableStateOf<Item?>(null) }

    fun openAddDialog() {
        editingItem = null
        nameInput = ""
        rateInput = ""
        gstRateInput = "18.0"
        unitInput = "Nos"
        descriptionInput = ""
        showItemDialog = true
    }

    fun openEditDialog(item: Item) {
        editingItem = item
        nameInput = item.name
        rateInput = item.rate.toString()
        gstRateInput = item.gstRate.toString()
        unitInput = item.unit
        descriptionInput = item.description ?: ""
        showItemDialog = true
    }

    // Stats calculations
    val totalItems = items.size
    val activeItems = items.size // All registered items in inventory are active
    val inactiveItems = 0
    val activePercentage = if (totalItems > 0) 100 else 100
    val inactivePercentage = 0
    val totalValue = items.sumOf { it.rate }

    // Filter and search
    val filteredItems = items.filter { item ->
        val matchesQuery = searchQuery.isBlank() ||
                item.name.contains(searchQuery, ignoreCase = true) ||
                (item.description?.contains(searchQuery, ignoreCase = true) == true) ||
                (item.unit.contains(searchQuery, ignoreCase = true))

        val matchesFilter = when (filterOption) {
            "GST 18%" -> item.gstRate == 18.0
            "GST 12%" -> item.gstRate == 12.0
            "GST 5%" -> item.gstRate == 5.0
            "GST 0%" -> item.gstRate == 0.0
            else -> true
        }

        matchesQuery && matchesFilter
    }

    // Add / Edit Modal Dialog
    if (showItemDialog) {
        AlertDialog(
            onDismissRequest = { showItemDialog = false },
            containerColor = SurfaceCard,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = if (editingItem == null) stringResource(Res.string.add_item) else "Edit Item",
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
                        label = { Text(stringResource(Res.string.item_name) + " *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = tfColors
                    )
                    OutlinedTextField(
                        value = rateInput,
                        onValueChange = { rateInput = it },
                        label = { Text(stringResource(Res.string.rate) + " ($currency) *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = tfColors
                    )
                    OutlinedTextField(
                        value = gstRateInput,
                        onValueChange = { gstRateInput = it },
                        label = { Text(stringResource(Res.string.tax_rate)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = tfColors
                    )
                    OutlinedTextField(
                        value = unitInput,
                        onValueChange = { unitInput = it },
                        label = { Text("Unit (e.g. Nos, Pcs, Kg, Hours)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = tfColors
                    )
                    OutlinedTextField(
                        value = descriptionInput,
                        onValueChange = { descriptionInput = it },
                        label = { Text("Description (Optional)") },
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
                        val rateVal = rateInput.toDoubleOrNull()
                        val gstVal = gstRateInput.toDoubleOrNull() ?: 18.0
                        if (nameInput.isNotBlank() && rateVal != null) {
                            if (editingItem != null) {
                                viewModel.updateItem(
                                    editingItem!!.copy(
                                        name = nameInput.trim(),
                                        rate = rateVal,
                                        gstRate = gstVal,
                                        unit = unitInput.trim().ifEmpty { "Nos" },
                                        description = descriptionInput.trim().ifEmpty { null }
                                    )
                                )
                            } else {
                                viewModel.addItem(
                                    name = nameInput.trim(),
                                    rate = rateVal,
                                    gstRate = gstVal,
                                    unit = unitInput.trim().ifEmpty { "Nos" },
                                    description = descriptionInput.trim().ifEmpty { null }
                                )
                            }
                            showItemDialog = false
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                ) {
                    Text(stringResource(Res.string.save), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showItemDialog = false }) {
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
            title = { Text("Filter Items", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("All", "GST 18%", "GST 12%", "GST 5%", "GST 0%").forEach { opt ->
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
    itemToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            containerColor = SurfaceCard,
            shape = RoundedCornerShape(20.dp),
            title = { Text("Delete Item", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = { Text("Are you sure you want to delete ${item.name}?", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteItem(item)
                        itemToDelete = null
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StatRed)
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }


    Scaffold(
        containerColor = ScreenBackground,
        bottomBar = {
            AppBottomNavigationBar(
                currentRoute = Screen.Items.route,
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
                    contentDescription = stringResource(Res.string.add_item),
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
                    title = "Items",
                    subtitle = "Manage your products and services.",
                    trailingContent = {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PrimaryPurpleLight)
                                .clickable { openAddDialog() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_items),
                                contentDescription = "Items",
                                tint = PrimaryPurple,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                )
            }

            // 2. Stat Cards Row (4 cards)
            item {
                val statCards = listOf(
                    StatCardData(
                        title = "Total Items",
                        value = "$totalItems",
                        subValue = "All items",
                        iconPainter = Res.drawable.ic_items,
                        iconBgColor = StatPurpleLight,
                        iconTintColor = StatPurple,
                        subValueColor = StatPurple
                    ),
                    StatCardData(
                        title = "Active Items",
                        value = "$activeItems",
                        subValue = "$activePercentage%",
                        icon = Icons.Default.CheckCircle,
                        iconBgColor = StatGreenLight,
                        iconTintColor = StatGreen,
                        subValueColor = StatGreen
                    ),
                    StatCardData(
                        title = "Inactive Items",
                        value = "$inactiveItems",
                        subValue = "$inactivePercentage%",
                        icon = Icons.Default.Inventory2,
                        iconBgColor = StatOrangeLight,
                        iconTintColor = StatOrange,
                        subValueColor = StatOrange
                    ),
                    StatCardData(
                        title = "Total Value",
                        value = platform.formatCurrency(totalValue, currency),
                        subValue = "This Month",
                        icon = Icons.Default.AttachMoney,
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
                    placeholder = "Search items...",
                    onFilterClick = { showFilterDialog = true },
                    isFilterActive = filterOption != "All"
                )
            }

            // 4. Items List
            if (filteredItems.isEmpty()) {
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
                                text = if (searchQuery.isNotEmpty()) "No matching items found" else "No items added yet. Tap + to add one.",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = TextSecondary
                                )
                            )
                        }
                    }
                }
            } else {
                items(filteredItems) { item ->
                    ItemCardItem(
                        item = item,
                        currency = currency,
                        onEdit = { openEditDialog(item) },
                        onDelete = { itemToDelete = item }
                    )
                }
            }
        }
    }
}

@Composable
fun ItemCardItem(
    item: Item,
    currency: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val platform = getPlatform()
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
            // Left: Icon Box + Details
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Purple Icon Box
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PrimaryPurpleLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = PrimaryPurple,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = item.name,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )

                    // Rate Row with Price Tag icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sell,
                            contentDescription = "Rate",
                            tint = PrimaryPurple,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "Rate: ${platform.formatCurrency(item.rate, currency)}",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary
                            )
                        )
                    }

                    // Tax GST Row with Percentage icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Percent,
                            contentDescription = "Tax",
                            tint = PrimaryPurple,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "Tax (GST): ${item.gstRate}%",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Active Badge
                    StatusBadge(
                        status = BadgeStatus.ACTIVE,
                        customText = "Active"
                    )
                }
            }

            // Right: Actions (3 dots menu + Red Trash button)
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.height(84.dp)
            ) {
                // 3 dots menu
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
                            text = { Text("Edit Item") },
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

                // Red Trash Button
                Box(
                    modifier = Modifier
                        .size(30.dp)
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

