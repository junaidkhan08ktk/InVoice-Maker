package com.example.invoicegenerator

import androidx.compose.runtime.Composable
import com.example.invoicegenerator.data.entity.Business
import com.example.invoicegenerator.data.entity.Customer
import com.example.invoicegenerator.data.entity.Invoice
import com.example.invoicegenerator.data.entity.InvoiceItem
import com.example.invoicegenerator.domain.pdf.TemplateType

interface Platform {
    val name: String
    fun shareFile(path: String, title: String)
    fun showToast(message: String)
    fun generateAndShareInvoice(
        business: Business,
        customer: Customer,
        invoice: Invoice,
        items: List<InvoiceItem>,
        template: TemplateType,
        isPro: Boolean,
        currency: String,
        labels: com.example.invoicegenerator.domain.pdf.PdfLabels
    )
    fun formatDate(timestamp: Long): String
    fun formatCurrency(amount: Double, currency: String): String
    fun setLanguage(languageCode: String)
}

expect fun getPlatform(): Platform

@Composable
expect fun BackHandler(onBack: () -> Unit)
