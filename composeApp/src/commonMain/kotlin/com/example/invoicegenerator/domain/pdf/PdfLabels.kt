package com.example.invoicegenerator.domain.pdf

data class PdfLabels(
    val gstin: String,
    val invoiceNumber: String,
    val date: String,
    val billTo: String,
    val description: String,
    val qty: String,
    val rate: String,
    val total: String,
    val subtotal: String,
    val cgst: String,
    val sgst: String,
    val grandTotal: String,
    val watermark: String
)
