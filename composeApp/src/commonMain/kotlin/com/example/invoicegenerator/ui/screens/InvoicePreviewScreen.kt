package com.example.invoicegenerator.ui.screens

import com.example.invoicegenerator.getPlatform

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import com.example.invoicegenerator.viewmodel.InvoiceViewModel

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.invoicegenerator.getPlatform
import com.example.invoicegenerator.viewmodel.BusinessViewModel
import com.example.invoicegenerator.viewmodel.InvoiceWithItems

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicePreviewScreen(
    invoiceId: Long,
    onBack: () -> Unit,
    viewModel: InvoiceViewModel = koinViewModel(),
    businessViewModel: BusinessViewModel = koinViewModel()
) {
    val platform = getPlatform()
    val isPro by viewModel.isPro.collectAsState(initial = false)
    val invoiceData by viewModel.getInvoiceById(invoiceId).collectAsState(initial = null)
    val businessProfile by businessViewModel.businessProfile.collectAsState()
    var selectedTemplate by remember { mutableStateOf(com.example.invoicegenerator.domain.pdf.TemplateType.CLASSIC) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Invoice Preview") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val b = businessProfile
                        val d = invoiceData
                        if (b != null && d != null) {
                            platform.generateAndShareInvoice(
                                business = b,
                                customer = d.customer ?: com.example.invoicegenerator.data.entity.Customer(name = "Unknown"),
                                invoice = d.invoice,
                                items = d.items,
                                template = selectedTemplate,
                                isPro = isPro
                            )
                        }
                    }) {
                        Icon(Icons.Default.Share, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        if (invoiceData == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val data = invoiceData!!
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Template Selector
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    com.example.invoicegenerator.domain.pdf.TemplateType.values().forEach { template ->
                        FilterChip(
                            selected = selectedTemplate == template,
                            onClick = { selectedTemplate = template },
                            label = { Text(template.name) }
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .padding(if (selectedTemplate == com.example.invoicegenerator.domain.pdf.TemplateType.THERMAL) 8.dp else 24.dp)
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            when (selectedTemplate) {
                                com.example.invoicegenerator.domain.pdf.TemplateType.CLASSIC -> {
                                    ClassicPreview(businessProfile, data)
                                }
                                com.example.invoicegenerator.domain.pdf.TemplateType.MODERN -> {
                                    ModernPreview(businessProfile, data)
                                }
                                com.example.invoicegenerator.domain.pdf.TemplateType.THERMAL -> {
                                    ThermalPreview(businessProfile, data)
                                }
                            }
                        }

                        if (!isPro) {
                            Text(
                                text = "FREE VERSION - WATERMARKED",
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .graphicsLayer(alpha = 0.1f, rotationZ = -45f),
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val b = businessProfile
                        val d = invoiceData
                        if (b != null && d != null) {
                            platform.generateAndShareInvoice(
                                business = b,
                                customer = d.customer ?: com.example.invoicegenerator.data.entity.Customer(name = "Unknown"),
                                invoice = d.invoice,
                                items = d.items,
                                template = selectedTemplate,
                                isPro = isPro
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Share/Download PDF")
                }
            }
        }
    }
}

@Composable
fun ClassicPreview(business: com.example.invoicegenerator.data.entity.Business?, data: InvoiceWithItems) {
    val platform = getPlatform()
    Text(
        text = business?.name ?: "Business Name",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
    )
    Text(text = "GSTIN: ${business?.gstin ?: "N/A"}")
    Spacer(modifier = Modifier.height(8.dp))
    Text(text = "Customer: ${data.customer?.name ?: "N/A"}")
    if (data.customer?.gstin != null) {
        Text(text = "Customer GSTIN: ${data.customer.gstin}")
    }
    Spacer(modifier = Modifier.height(16.dp))
    HorizontalDivider()
    Spacer(modifier = Modifier.height(16.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = "Invoice: ${data.invoice.invoiceNumber}", fontWeight = FontWeight.Bold)
        Text(text = "Date: ${platform.formatDate(data.invoice.date)}")
    }
    Spacer(modifier = Modifier.height(24.dp))
    
    data.items.forEach { item ->
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "${item.itemName} x ${item.quantity}")
            Text(text = platform.formatCurrency(item.total))
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
    
    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
    
    TotalRow("Subtotal", platform.formatCurrency(data.invoice.subTotal))
    TotalRow("Tax (GST)", platform.formatCurrency(data.invoice.cgst + data.invoice.sgst + data.invoice.igst))
    TotalRow("Grand Total", platform.formatCurrency(data.invoice.totalAmount), isBold = true)
}

@Composable
fun ModernPreview(business: com.example.invoicegenerator.data.entity.Business?, data: InvoiceWithItems) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = (business?.name ?: "Business Name").uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(text = "GSTIN: ${business?.gstin ?: "N/A"}", style = MaterialTheme.typography.labelMedium)
        }
    }
    Spacer(modifier = Modifier.height(24.dp))
    ClassicPreview(business = null, data = data) // Reuse item list and totals
}

@Composable
fun ThermalPreview(business: com.example.invoicegenerator.data.entity.Business?, data: InvoiceWithItems) {
    val platform = getPlatform()
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(text = business?.name ?: "Business Name", fontWeight = FontWeight.Bold)
        Text(text = data.invoice.invoiceNumber, style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))
        data.items.forEach { item ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "${item.itemName} x ${item.quantity}", style = MaterialTheme.typography.bodySmall)
                Text(text = platform.formatCurrency(item.total), style = MaterialTheme.typography.bodySmall)
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        Text(
            text = "Total: ${platform.formatCurrency(data.invoice.totalAmount)}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
fun TotalRow(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
        Text(text = value, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
    }
}
