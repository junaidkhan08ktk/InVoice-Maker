package com.example.invoicegenerator.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.invoicegenerator.Res
import com.example.invoicegenerator.*
import com.example.invoicegenerator.dashboard
import com.example.invoicegenerator.getPlatform
import com.example.invoicegenerator.new_invoice
import com.example.invoicegenerator.no_invoices
import com.example.invoicegenerator.recent_invoices
import com.example.invoicegenerator.ui.components.*
import com.example.invoicegenerator.ui.navigation.Screen
import com.example.invoicegenerator.ui.theme.*
import com.example.invoicegenerator.viewmodel.DashboardViewModel
import com.example.invoicegenerator.viewmodel.InvoiceWithCustomer
import com.example.invoicegenerator.viewmodel.SettingsViewModel
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNewInvoice: () -> Unit,
    onNavigateTo: (String) -> Unit,
    viewModel: DashboardViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val stats by viewModel.stats.collectAsState()
    val currency by settingsViewModel.currency.collectAsState(initial = "USD")
    val platform = getPlatform()

    // Period selector states
    var revenuePeriodMenuExpanded by remember { mutableStateOf(false) }
    var selectedRevenuePeriod by remember { mutableStateOf("This Month") }

    var chartPeriodMenuExpanded by remember { mutableStateOf(false) }
    var selectedChartPeriod by remember { mutableStateOf("This Week") }

    // Dynamic greeting based on hour
    val greetingName = stats.businessProfile?.name ?: "User"
    val greeting = "Good Morning, $greetingName \uD83D\uDC4B"

    Scaffold(
        containerColor = ScreenBackground,
        bottomBar = {
            AppBottomNavigationBar(
                currentRoute = Screen.Dashboard.route,
                onNavigate = onNavigateTo
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewInvoice,
                shape = CircleShape,
                containerColor = PrimaryPurple,
                contentColor = Color.White,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(Res.string.new_invoice),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
           // contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Top Bar & Greeting Area
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {

                    // Action Icons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { /* Drawer / Menu action */ },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = TextPrimary
                            )
                        }

                        // Notification Bell with Unread Dot
                        Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                            IconButton(onClick = { /* Notifications */ }) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = TextPrimary
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-4).dp, y = 4.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryPurple)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = greeting,
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Here's what's happening with your business today.",
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    )
                }
            }

            // 2. Hero Total Revenue Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            PrimaryPurpleGradientStart,
                                            PrimaryPurpleGradientEnd
                                        )
                                    )
                                )
                        ) {
                            // Wave decoration in background
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height
                                val path = Path().apply {
                                    moveTo(0f, h * 0.75f)
                                    cubicTo(
                                        w * 0.25f, h * 0.90f,
                                        w * 0.65f, h * 0.60f,
                                        w, h * 0.82f
                                    )
                                    lineTo(w, h)
                                    lineTo(0f, h)
                                    close()
                                }
                                drawPath(
                                    path = path,
                                    color = Color.White.copy(alpha = 0.12f)
                                )

                                val waveLine = Path().apply {
                                    moveTo(0f, h * 0.70f)
                                    cubicTo(
                                        w * 0.3f, h * 0.85f,
                                        w * 0.7f, h * 0.55f,
                                        w, h * 0.78f
                                    )
                                }
                                drawPath(
                                    path = waveLine,
                                    color = Color.White.copy(alpha = 0.25f),
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }

                            // Content overlay
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxHeight(),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Total Revenue",
                                        style = TextStyle(
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.White.copy(alpha = 0.85f)
                                        )
                                    )

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = platform.formatCurrency(stats.totalSales, currency),
                                            style = TextStyle(
                                                fontSize = 30.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(Color.White.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.BarChart,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    // Dropdown selector
                                    Box {
                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable { revenuePeriodMenuExpanded = true }
                                                .padding(vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = selectedRevenuePeriod,
                                                style = TextStyle(
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = Color.White.copy(alpha = 0.9f)
                                                )
                                            )
                                            Icon(
                                                imageVector = Icons.Default.ArrowDropDown,
                                                contentDescription = null,
                                                tint = Color.White.copy(alpha = 0.9f),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        DropdownMenu(
                                            expanded = revenuePeriodMenuExpanded,
                                            onDismissRequest = { revenuePeriodMenuExpanded = false }
                                        ) {
                                            listOf("This Week", "This Month", "This Year", "All Time").forEach { period ->
                                                DropdownMenuItem(
                                                    text = { Text(period) },
                                                    onClick = {
                                                        selectedRevenuePeriod = period
                                                        revenuePeriodMenuExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                // Wallet icon card on right
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.White.copy(alpha = 0.25f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalanceWallet,
                                        contentDescription = "Wallet",
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Stat Cards Row (4 Cards)
            item {
                val statCards = listOf(
                    StatCardData(
                        title = "Paid Invoices",
                        value = "${stats.paidInvoicesCount}",
                        subValue = platform.formatCurrency(stats.paidInvoicesAmount, currency),
                        icon = Icons.Default.CheckCircle,
                        iconBgColor = StatGreenLight,
                        iconTintColor = StatGreen,
                        subValueColor = StatGreen
                    ),
                    StatCardData(
                        title = "Unpaid Invoices",
                        value = "${stats.unpaidInvoicesCount}",
                        subValue = platform.formatCurrency(stats.unpaidInvoicesAmount, currency),
                        icon = Icons.Default.AccessTime,
                        iconBgColor = StatOrangeLight,
                        iconTintColor = StatOrange,
                        subValueColor = StatOrange
                    ),
                    StatCardData(
                        title = "Total Customers",
                        value = "${stats.totalCustomersCount}",
                        subValue = "+${stats.totalCustomersCount} this month",
                        iconPainter = Res.drawable.ic_customer_bn,
                        iconBgColor = StatBlueLight,
                        iconTintColor = StatBlue,
                        subValueColor = StatBlue
                    ),
                    StatCardData(
                        title = "Total Items",
                        value = "${stats.totalItemsCount}",
                        subValue = "${stats.totalItemsCount} in stock",
                        iconPainter = Res.drawable.ic_items,
                        iconBgColor = StatPurpleLight,
                        iconTintColor = StatPurple,
                        subValueColor = StatPurple
                    )
                )

                StatCardsRow(cards = statCards)
            }

            // 4. Revenue Overview Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Section header with dropdown
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Revenue Overview",
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )

                                Box {
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { chartPeriodMenuExpanded = true },
                                        shape = RoundedCornerShape(8.dp),
                                        color = ScreenBackground
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = selectedChartPeriod,
                                                style = TextStyle(
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = TextSecondary
                                                )
                                            )
                                            Icon(
                                                imageVector = Icons.Default.ArrowDropDown,
                                                contentDescription = null,
                                                tint = TextSecondary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    DropdownMenu(
                                        expanded = chartPeriodMenuExpanded,
                                        onDismissRequest = { chartPeriodMenuExpanded = false }
                                    ) {
                                        listOf("This Week", "This Month", "This Year").forEach { period ->
                                            DropdownMenuItem(
                                                text = { Text(period) },
                                                onClick = {
                                                    selectedChartPeriod = period
                                                    chartPeriodMenuExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Canvas Line Chart
                            RevenueLineChart(
                                weeklyData = stats.weeklyRevenue,
                                chartColor = PrimaryPurple
                            )
                        }
                    }
                }
            }

            // 5. Recent Invoices Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.recent_invoices),
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = stringResource(Res.string.view_all),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryPurple
                        ),
                        modifier = Modifier.clickable { onNavigateTo(Screen.Invoices.route) }
                    )
                }
            }

            // 6. Recent Invoices List
            if (stats.recentInvoices.isEmpty()) {
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
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(Res.string.no_invoices),
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = TextSecondary
                                )
                            )
                        }
                    }
                }
            } else {
                items(stats.recentInvoices) { itemData ->
                    DashboardInvoiceCard(
                        invoiceData = itemData,
                        currency = currency,
                        onClick = { onNavigateTo(Screen.InvoicePreview.createRoute(itemData.invoice.id)) },
                        onToggleStatus = { viewModel.toggleInvoiceStatus(itemData.invoice) }
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardInvoiceCard(
    invoiceData: InvoiceWithCustomer,
    currency: String,
    onClick: () -> Unit,
    onToggleStatus: () -> Unit
) {
    val invoice = invoiceData.invoice
    val customerName = invoiceData.customer?.name ?: "Client Name"
    val initials = getInitials(customerName)
    val platform = getPlatform()

    // Format date e.g. Due 20 May 2025
    val formattedDate = try {
        val instant = Instant.fromEpochMilliseconds(invoice.date)
        val dt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val monthName = dt.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
        "Due ${dt.dayOfMonth} $monthName ${dt.year}"
    } catch (e: Exception) {
        "Due Date"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable { onClick() },
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
            // Left: Initials Avatar Box + Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Avatar box
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PrimaryPurpleLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryPurple
                        )
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Inv #${invoice.invoiceNumber}",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        ),
                        maxLines = 1
                    )
                    Text(
                        text = customerName,
                        style = TextStyle(
                            fontSize = 13.sp,
                            color = TextSecondary
                        ),
                        maxLines = 1
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = formattedDate,
                            style = TextStyle(
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        )
                    }
                }
            }

            // Right: Amount + Status Pill
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = platform.formatCurrency(invoice.totalAmount, currency),
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )

                Box(modifier = Modifier.clickable { onToggleStatus() }) {
                    StatusBadge(
                        status = if (invoice.isPaid) BadgeStatus.PAID else BadgeStatus.UNPAID
                    )
                }
            }
        }
    }
}

private fun getInitials(name: String): String {
    val trimmed = name.trim()
    if (trimmed.isEmpty()) return "CL"
    val parts = trimmed.split(" ").filter { it.isNotBlank() }
    return if (parts.size >= 2) {
        "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
    } else {
        trimmed.take(2).uppercase()
    }
}

