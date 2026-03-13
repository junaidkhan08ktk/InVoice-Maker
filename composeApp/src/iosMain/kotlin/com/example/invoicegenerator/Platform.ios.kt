package com.example.invoicegenerator

import androidx.compose.runtime.Composable
import platform.UIKit.UIDevice
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumber
import platform.UIKit.UIViewController
import platform.UIKit.UIActivityViewController

class IOSPlatform : Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion()
    
    override fun shareFile(path: String, title: String) {
        // Basic share logic for iOS
        // Need to find the current UIViewController
    }

    override fun showToast(message: String) {
        // iOS doesn't have Toast
    }

    override fun generateAndShareInvoice(
        business: com.example.invoicegenerator.data.entity.Business,
        customer: com.example.invoicegenerator.data.entity.Customer,
        invoice: com.example.invoicegenerator.data.entity.Invoice,
        items: List<com.example.invoicegenerator.data.entity.InvoiceItem>,
        template: com.example.invoicegenerator.domain.pdf.TemplateType,
        isPro: Boolean,
        currency: String
    ) {
        // TODO: Implement PDF generation for iOS
    }

    private fun getCurrencySymbol(currency: String): String {
        return when (currency) {
            "INR" -> "₹"
            "USD" -> "$"
            "EUR" -> "€"
            "GBP" -> "£"
            "PKR" -> "Rs "
            "SAR" -> "SR "
            "BDT" -> "৳"
            "BRL" -> "R$"
            "RUB" -> "₽"
            else -> "$currency "
        }
    }

    override fun formatDate(timestamp: Long): String {
        val date = NSDate.dateWithTimeIntervalSince1970(timestamp / 1000.0)
        val formatter = NSDateFormatter()
        formatter.dateFormat = "dd/MM/yyyy"
        return formatter.stringFromDate(date)
    }

    override fun formatCurrency(amount: Double, currency: String): String {
        val symbol = getCurrencySymbol(currency)
        val formatter = NSNumberFormatter()
        formatter.numberStyle = platform.Foundation.NSNumberFormatterDecimalStyle
        formatter.minimumFractionDigits = 2u
        formatter.maximumFractionDigits = 2u
        return "$symbol${formatter.stringFromNumber(NSNumber(amount)) ?: "0.00"}"
    }
}

actual fun getPlatform(): Platform = IOSPlatform()

@Composable
actual fun BackHandler(onBack: () -> Unit) {
    // iOS doesn't have a hardware back button, usually handled by navigation
}
