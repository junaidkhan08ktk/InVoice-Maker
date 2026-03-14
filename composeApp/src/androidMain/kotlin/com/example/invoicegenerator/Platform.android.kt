package com.example.invoicegenerator

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.core.content.FileProvider
import com.example.invoicegenerator.data.entity.Business
import com.example.invoicegenerator.data.entity.Customer
import com.example.invoicegenerator.data.entity.Invoice
import com.example.invoicegenerator.data.entity.InvoiceItem
import com.example.invoicegenerator.domain.pdf.PdfGenerator
import com.example.invoicegenerator.domain.pdf.TemplateType
import java.io.File

class AndroidPlatform(private val context: Context) : Platform {
    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
    
    override fun shareFile(path: String, title: String) {
        val file = File(path)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooserIntent = Intent.createChooser(intent, title)
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooserIntent)
    }

    override fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun generateAndShareInvoice(
        business: Business,
        customer: Customer,
        invoice: Invoice,
        items: List<InvoiceItem>,
        template: TemplateType,
        isPro: Boolean,
        currency: String,
        labels: com.example.invoicegenerator.domain.pdf.PdfLabels
    ) {
        val pdfGen = PdfGenerator(context)
        val symbol = getCurrencySymbol(currency)
        val file = pdfGen.generateInvoicePdf(
            business,
            customer,
            invoice,
            items,
            template,
            showWatermark = !isPro,
            currencySymbol = symbol,
            labels = labels
        )
        if (file != null) {
            shareFile(file.absolutePath, "Share Invoice")
        }
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
        return java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date(timestamp))
    }

    override fun formatCurrency(amount: Double, currency: String): String {
        val symbol = getCurrencySymbol(currency)
        return "$symbol${String.format("%.2f", amount)}"
    }

    override fun setLanguage(languageCode: String) {
        val appLocale: androidx.core.os.LocaleListCompat = androidx.core.os.LocaleListCompat.forLanguageTags(languageCode)
        androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(appLocale)
    }
}

actual fun getPlatform(): Platform = AndroidPlatform(InvoiceApplication.instance.applicationContext)

@Composable
actual fun BackHandler(onBack: () -> Unit) {
    androidx.activity.compose.BackHandler(onBack = onBack)
}
