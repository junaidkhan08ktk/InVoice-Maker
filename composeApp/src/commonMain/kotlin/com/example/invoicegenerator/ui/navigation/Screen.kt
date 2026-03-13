package com.example.invoicegenerator.ui.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object BusinessSetup : Screen("business_setup")
    object Dashboard : Screen("dashboard")
    object CreateInvoice : Screen("create_invoice")
    object InvoicePreview : Screen("invoice_preview/{invoiceId}") {
        fun createRoute(invoiceId: Long) = "invoice_preview/$invoiceId"
    }
    object Invoices : Screen("invoices")
    object Customers : Screen("customers")
    object Items : Screen("items")
    object Settings : Screen("settings")
    object LanguageSelection : Screen("language_selection")
}
