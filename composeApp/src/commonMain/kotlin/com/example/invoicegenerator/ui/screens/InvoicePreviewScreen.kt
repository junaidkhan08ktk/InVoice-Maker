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
import com.example.invoicegenerator.viewmodel.BusinessViewModel
import com.example.invoicegenerator.viewmodel.InvoiceWithItems
import com.example.invoicegenerator.viewmodel.SettingsViewModel

import org.jetbrains.compose.resources.stringResource
import com.example.invoicegenerator.Res
import com.example.invoicegenerator.*
import com.example.invoicegenerator.invoice_preview
import com.example.invoicegenerator.share_download_pdf
import com.example.invoicegenerator.gstin
import com.example.invoicegenerator.date
import com.example.invoicegenerator.bill_to
import com.example.invoicegenerator.description
import com.example.invoicegenerator.qty
import com.example.invoicegenerator.rate
import com.example.invoicegenerator.total
import com.example.invoicegenerator.subtotal
import com.example.invoicegenerator.cgst
import com.example.invoicegenerator.sgst
import com.example.invoicegenerator.grand_total
import com.example.invoicegenerator.tax_gst
import com.example.invoicegenerator.invoice_number_label
import com.example.invoicegenerator.customer_label
import com.example.invoicegenerator.data.entity.Business
import com.example.invoicegenerator.data.entity.Customer
import com.example.invoicegenerator.unknown
import com.example.invoicegenerator.free_version_watermark
import com.example.invoicegenerator.domain.pdf.PdfLabels

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicePreviewScreen(
    invoiceId: Long,
    onBack: () -> Unit,

    ) {
    val viewModel: InvoiceViewModel = koinViewModel()
    val businessViewModel: BusinessViewModel = koinViewModel()
    val settingsViewModel: SettingsViewModel = koinViewModel()
    val platform = getPlatform()
    val isPro by viewModel.isPro.collectAsState(initial = false)
    val invoiceData by viewModel.getInvoiceById(invoiceId).collectAsState(initial = null)
    val businessProfile by businessViewModel.businessProfile.collectAsState()
    val currency by settingsViewModel.currency.collectAsState(initial = "USD")
    var selectedTemplate by remember { mutableStateOf(com.example.invoicegenerator.domain.pdf.TemplateType.CLASSIC) }

    val pdfLabels = PdfLabels(
        gstin = stringResource(Res.string.gstin),
        invoiceNumber = stringResource(Res.string.invoice_number_label),
        date = stringResource(Res.string.date),
        billTo = stringResource(Res.string.bill_to),
        description = stringResource(Res.string.description),
        qty = stringResource(Res.string.qty),
        rate = stringResource(Res.string.rate),
        total = stringResource(Res.string.total),
        subtotal = stringResource(Res.string.subtotal),
        cgst = stringResource(Res.string.cgst),
        sgst = stringResource(Res.string.sgst),
        grandTotal = stringResource(Res.string.grand_total),
        watermark = stringResource(Res.string.free_version_watermark)
    )

    val unknownLabel = stringResource(Res.string.unknown)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.invoice_preview)) },
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
                                customer = d.customer ?: Customer(name = unknownLabel),
                                invoice = d.invoice,
                                items = d.items,
                                template = selectedTemplate,
                                isPro = isPro,
                                currency = currency,
                                labels = pdfLabels
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
                    com.example.invoicegenerator.domain.pdf.TemplateType.values()
                        .forEach { template ->
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
                                    ClassicPreview(businessProfile, data, currency)
                                }

                                com.example.invoicegenerator.domain.pdf.TemplateType.MODERN -> {
                                    ModernPreview(businessProfile, data, currency)
                                }

                                com.example.invoicegenerator.domain.pdf.TemplateType.THERMAL -> {
                                    ThermalPreview(businessProfile, data, currency)
                                }
                            }
                        }

                        if (!isPro) {
                            Text(
                                text = stringResource(Res.string.free_version_watermark),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .graphicsLayer(alpha = 0.1f, rotationZ = -45f),
                                style = MaterialTheme.typography.displaySmall,
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
                                customer = d.customer ?: Customer(name = unknownLabel),
                                invoice = d.invoice,
                                items = d.items,
                                template = selectedTemplate,
                                isPro = isPro,
                                currency = currency,
                                labels = pdfLabels
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(Res.string.share_download_pdf))
                }
            }
        }
    }
}

@Composable
fun ClassicPreview(
    business: Business?,
    data: InvoiceWithItems,
    currency: String
) {
    val platform = getPlatform()
    Text(
        text = business?.name ?: stringResource(Res.string.unknown),
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
    )
    Text(text = "${stringResource(Res.string.gstin)}: ${business?.gstin ?: "N/A"}")
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "${stringResource(Res.string.customer_label)}: ${
            data.customer?.name ?: stringResource(
                Res.string.unknown
            )
        }"
    )
    if (data.customer?.gstin != null) {
        Text(text = "${stringResource(Res.string.gstin)}: ${data.customer.gstin}")
    }
    Spacer(modifier = Modifier.height(16.dp))
    HorizontalDivider()
    Spacer(modifier = Modifier.height(16.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = "${stringResource(Res.string.invoice_label)}: ${data.invoice.invoiceNumber}",
            fontWeight = FontWeight.Bold
        )
        Text(text = "${stringResource(Res.string.date)}: ${platform.formatDate(data.invoice.date)}")
    }
    Spacer(modifier = Modifier.height(24.dp))

    data.items.forEach { item ->
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "${item.itemName} x ${item.quantity}")
            Text(text = platform.formatCurrency(item.total, currency))
        }
        Spacer(modifier = Modifier.height(8.dp))
    }

    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

    TotalRow(
        stringResource(Res.string.subtotal),
        platform.formatCurrency(data.invoice.subTotal, currency)
    )
    TotalRow(
        stringResource(Res.string.tax_gst),
        platform.formatCurrency(data.invoice.cgst + data.invoice.sgst + data.invoice.igst, currency)
    )
    TotalRow(
        stringResource(Res.string.grand_total),
        platform.formatCurrency(data.invoice.totalAmount, currency),
        isBold = true
    )
}

@Composable
fun ModernPreview(
    business: com.example.invoicegenerator.data.entity.Business?,
    data: InvoiceWithItems,
    currency: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                alpha = 0.1f
            )
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = (business?.name ?: stringResource(Res.string.unknown)).uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "${stringResource(Res.string.gstin)}: ${business?.gstin ?: "N/A"}",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
    Spacer(modifier = Modifier.height(24.dp))
    ClassicPreview(business = null, data = data, currency = currency) // Reuse item list and totals
}

@Composable
fun ThermalPreview(
    business: Business?,
    data: InvoiceWithItems,
    currency: String
) {
    val platform = getPlatform()
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = business?.name ?: stringResource(Res.string.unknown),
            fontWeight = FontWeight.Bold
        )
        Text(text = data.invoice.invoiceNumber, style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))
        data.items.forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${item.itemName} x ${item.quantity}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = platform.formatCurrency(item.total, currency),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        Text(
            text = "${stringResource(Res.string.total)}: ${
                platform.formatCurrency(
                    data.invoice.totalAmount,
                    currency
                )
            }",
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
