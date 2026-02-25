package com.example.invoicegenerator

import androidx.compose.runtime.Composable

interface Platform {
    val name: String
    fun shareFile(path: String, title: String)
    fun showToast(message: String)
    fun generateAndShareInvoice(
        business: com.example.invoicegenerator.data.entity.Business,
        customer: com.example.invoicegenerator.data.entity.Customer,
        invoice: com.example.invoicegenerator.data.entity.Invoice,
        items: List<com.example.invoicegenerator.data.entity.InvoiceItem>,
        template: com.example.invoicegenerator.domain.pdf.TemplateType,
        isPro: Boolean,
        currency: String
    )
    fun formatDate(timestamp: Long): String
    fun formatCurrency(amount: Double, currency: String): String
}

expect fun getPlatform(): Platform

@Composable
expect fun BackHandler(onBack: () -> Unit)
