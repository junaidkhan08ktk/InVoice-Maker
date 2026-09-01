package com.example.invoicegenerator.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.invoicegenerator.Res
import com.example.invoicegenerator.*
import com.example.invoicegenerator.customers
import com.example.invoicegenerator.invoices
import com.example.invoicegenerator.items
import com.example.invoicegenerator.settings
import com.example.invoicegenerator.ui.navigation.Screen
import com.example.invoicegenerator.ui.theme.*
import com.example.invoicegenerator.viewmodel.DayRevenue
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * Standard Header matching the UI schema across all screens.
 */
@Composable
fun AppScreenHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 8.dp, end = 20.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {



        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 28.sp,
                    color = TextPrimary
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            )
        }
        if (trailingContent != null) {
            Spacer(modifier = Modifier.width(12.dp))
            trailingContent()
        }
    }
}

/**
 * Data class for consistent Stat Cards.
 */
data class StatCardData(
    val title: String,
    val value: String,
    val subValue: String,
    val icon: ImageVector? = null,
    val iconPainter: org.jetbrains.compose.resources.DrawableResource? = null,
    val iconBgColor: Color,
    val iconTintColor: Color,
    val subValueColor: Color,
    val cardBgColor: Color = SurfaceCard
)

/**
 * Individual Stat Card matching design.
 */
@Composable
fun StatCardItem(
    data: StatCardData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(125.dp)
            .height(130.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = data.cardBgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon container
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(data.iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                if (data.icon != null) {
                    Icon(
                        imageVector = data.icon,
                        contentDescription = null,
                        tint = data.iconTintColor,
                        modifier = Modifier.size(18.dp)
                    )
                } else if (data.iconPainter != null) {
                    Icon(
                        painter = painterResource(data.iconPainter),
                        contentDescription = null,
                        tint = data.iconTintColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column {
                Text(
                    text = data.title,
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    ),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = data.value,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    ),
                    maxLines = 1
                )
            }

            Text(
                text = data.subValue,
                style = TextStyle(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = data.subValueColor
                ),
                maxLines = 1
            )
        }
    }
}

/**
 * Scrollable row of Stat Cards.
 */
@Composable
fun StatCardsRow(
    cards: List<StatCardData>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        cards.forEach { cardData ->
            StatCardItem(data = cardData)
        }
    }
}

/**
 * Reusable Search Bar with Filter Button.
 */
@Composable
fun SearchBarWithFilter(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFilterActive: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Search text field
        Surface(
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(14.dp),
            color = SurfaceInput,
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceInputBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = TextMuted
                            )
                        )
                    }
                    BasicTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            color = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (query.isNotEmpty()) {
                    IconButton(
                        onClick = { onQueryChange("") },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Filter Button
        Surface(
            modifier = Modifier
                .height(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .clickable { onFilterClick() },
            shape = RoundedCornerShape(14.dp),
            color = if (isFilterActive) PrimaryPurpleLight else SurfaceCard,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isFilterActive) PrimaryPurple else SurfaceInputBorder
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Menu, // Using icon matching filter sliders
                    contentDescription = "Filter",
                    tint = if (isFilterActive) PrimaryPurple else TextPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Filter",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isFilterActive) PrimaryPurple else TextPrimary
                    )
                )
            }
        }
    }
}

/**
 * Status Badge for Invoices and Items (Paid, Unpaid, Active, Inactive).
 */
enum class BadgeStatus {
    PAID, UNPAID, ACTIVE, INACTIVE
}

@Composable
fun StatusBadge(
    status: BadgeStatus,
    modifier: Modifier = Modifier,
    customText: String? = null
) {
    val (bgColor, textColor, text, hasDot) = when (status) {
        BadgeStatus.PAID -> Quad(StatGreenLight, StatGreenDark, customText ?: "Paid", false)
        BadgeStatus.UNPAID -> Quad(StatOrangeLight, StatOrangeDark, customText ?: "Unpaid", false)
        BadgeStatus.ACTIVE -> Quad(StatGreenLight, StatGreenDark, customText ?: "Active", true)
        BadgeStatus.INACTIVE -> Quad(Color(0xFFF1F5F9), Color(0xFF64748B), customText ?: "Inactive", true)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (hasDot) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(textColor)
                )
            }
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
            )
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

/**
 * Revenue Line Chart matching design with smooth curve, dots, and gradient area.
 */
@Composable
fun RevenueLineChart(
    weeklyData: List<DayRevenue>,
    modifier: Modifier = Modifier,
    chartColor: Color = PrimaryPurple
) {
    val defaultDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val data = if (weeklyData.isNotEmpty()) {
        weeklyData
    } else {
        defaultDays.map { DayRevenue(it, 0.0) }
    }

    val maxVal = data.maxOfOrNull { it.amount } ?: 0.0
    val maxAmount = if (maxVal > 0.0) maxVal.coerceAtLeast(300.0) else 300.0
    val yStep = maxAmount / 3.0


    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val paddingLeft = 50.dp.toPx()
                val paddingBottom = 24.dp.toPx()
                val paddingTop = 12.dp.toPx()
                val paddingRight = 16.dp.toPx()

                val chartWidth = width - paddingLeft - paddingRight
                val chartHeight = height - paddingTop - paddingBottom

                // Draw horizontal dotted grid lines & labels
                for (i in 0..3) {
                    val yVal = maxAmount - (i * yStep)
                    val y = paddingTop + (i * (chartHeight / 3f))

                    // Dotted line
                    drawLine(
                        color = Color(0xFFE5E7EB),
                        start = Offset(paddingLeft, y),
                        end = Offset(width - paddingRight, y),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                }

                if (data.size > 1) {
                    val points = data.mapIndexed { index, item ->
                        val x = paddingLeft + (index.toFloat() / (data.size - 1)) * chartWidth
                        val normY = (item.amount / maxAmount).coerceIn(0.0, 1.0).toFloat()
                        val y = paddingTop + chartHeight - (normY * chartHeight)
                        Offset(x, y)
                    }

                    // Build path
                    val path = Path()
                    val fillPath = Path()

                    path.moveTo(points.first().x, points.first().y)
                    fillPath.moveTo(points.first().x, paddingTop + chartHeight)
                    fillPath.lineTo(points.first().x, points.first().y)

                    for (i in 0 until points.size - 1) {
                        val p0 = points[i]
                        val p1 = points[i + 1]
                        val controlPoint1 = Offset(p0.x + (p1.x - p0.x) / 2f, p0.y)
                        val controlPoint2 = Offset(p0.x + (p1.x - p0.x) / 2f, p1.y)

                        path.cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, p1.x, p1.y)
                        fillPath.cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, p1.x, p1.y)
                    }

                    fillPath.lineTo(points.last().x, paddingTop + chartHeight)
                    fillPath.close()

                    // Draw gradient fill
                    val gradient = Brush.verticalGradient(
                        colors = listOf(
                            chartColor.copy(alpha = 0.35f),
                            chartColor.copy(alpha = 0.02f)
                        ),
                        startY = paddingTop,
                        endY = paddingTop + chartHeight
                    )
                    drawPath(path = fillPath, brush = gradient)

                    // Draw stroke line
                    drawPath(
                        path = path,
                        color = chartColor,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw points
                    points.forEach { point ->
                        drawCircle(
                            color = chartColor,
                            radius = 5.dp.toPx(),
                            center = point
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 2.5.dp.toPx(),
                            center = point
                        )
                    }
                }
            }

            // Y-Axis Labels overlay on left
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(bottom = 24.dp, top = 8.dp, start = 8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                for (i in 3 downTo 0) {
                    val labelVal = (i * (maxAmount / 3)).toInt()
                    Text(
                        text = "$$labelVal",
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                    )
                }
            }
        }

        // X-Axis day labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 48.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            data.forEach { item ->
                Text(
                    text = item.day,
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                )
            }
        }
    }
}

/**
 * Unified Bottom Navigation Bar with purple active indicator underline.
 */
@Composable
fun AppBottomNavigationBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceCard,
        shadowElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(64.dp)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Dashboard / Invoices item
            val isDashboardSelected = currentRoute == Screen.Dashboard.route || currentRoute == Screen.Invoices.route
            BottomNavItem(
                title = stringResource(Res.string.invoices),
                iconVector = Icons.Default.DateRange, // Document/invoice icon
                iconPainter = Res.drawable.ic_invoice_bn,
                isSelected = isDashboardSelected,
                onClick = { onNavigate(Screen.Dashboard.route) }
            )

            // Customers item
            BottomNavItem(
                title = stringResource(Res.string.customers),
                iconPainter = Res.drawable.ic_customer_bn,
                isSelected = currentRoute == Screen.Customers.route,
                onClick = { onNavigate(Screen.Customers.route) }
            )

            // Items item
            BottomNavItem(
                title = stringResource(Res.string.items),
                iconPainter = Res.drawable.ic_items,
                isSelected = currentRoute == Screen.Items.route,
                onClick = { onNavigate(Screen.Items.route) }
            )

            // Settings item
            BottomNavItem(
                title = stringResource(Res.string.settings),
                iconPainter = Res.drawable.ic_setting,
                isSelected = currentRoute == Screen.Settings.route,
                onClick = { onNavigate(Screen.Settings.route) }
            )
        }
    }
}


@Composable
private fun BottomNavItem(
    title: String,
    iconPainter: org.jetbrains.compose.resources.DrawableResource? = null,
    iconVector: ImageVector? = null,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val tint = if (isSelected) PrimaryPurple else Color(0xFF9CA3AF)

    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (iconPainter != null) {
            Icon(
                painter = painterResource(iconPainter),
                contentDescription = title,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
        } else if (iconVector != null) {
            Icon(
                imageVector = iconVector,
                contentDescription = title,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = title,
            style = TextStyle(
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = tint
            )
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Active indicator line
        Box(
            modifier = Modifier
                .width(28.dp)
                .height(2.5.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(if (isSelected) PrimaryPurple else Color.Transparent)
        )
    }
}
