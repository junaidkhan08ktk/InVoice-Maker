package com.example.invoicegenerator.domain.pdf

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.example.invoicegenerator.data.entity.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*



class PdfGenerator(private val context: Context) {

    fun generateInvoicePdf(
        business: Business,
        customer: Customer,
        invoice: Invoice,
        items: List<InvoiceItem>,
        templateType: TemplateType = TemplateType.CLASSIC,
        showWatermark: Boolean = false,
        currencySymbol: String = "₹"
    ): File? {
        val pdfDocument = PdfDocument()
        val width = if (templateType == TemplateType.THERMAL) 226 else 595 // Thermal is approx 80mm
        val height = 842
        val pageInfo = PdfDocument.PageInfo.Builder(width, height, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()
        val boldPaint = Paint().apply { isFakeBoldText = true }

        when (templateType) {
            TemplateType.CLASSIC -> drawClassic(canvas, paint, boldPaint, business, customer, invoice, items, currencySymbol)
            TemplateType.MODERN -> drawModern(canvas, paint, boldPaint, business, customer, invoice, items, currencySymbol)
            TemplateType.THERMAL -> drawThermal(canvas, paint, boldPaint, business, customer, invoice, items, currencySymbol)
        }

        if (showWatermark) {
            drawWatermark(canvas)
        }

        pdfDocument.finishPage(page)
        val file = File(context.cacheDir, "invoice_${invoice.invoiceNumber}.pdf")
        return try {
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()
            file
        } catch (e: Exception) {
            pdfDocument.close()
            null
        }
    }

    private fun drawClassic(canvas: Canvas, paint: Paint, boldPaint: Paint, b: Business, c: Customer, i: Invoice, items: List<InvoiceItem>, currencySymbol: String) {
        var currentY = 50f
        paint.textSize = 18f
        canvas.drawText(b.name, 50f, currentY, paint)
        currentY += 25f
        paint.textSize = 12f
        canvas.drawText("GSTIN: ${b.gstin}", 50f, currentY, paint)
        currentY += 15f
        canvas.drawText(b.address, 50f, currentY, paint)
        currentY += 40f
        canvas.drawText("Invoice Number: ${i.invoiceNumber}", 50f, currentY, paint)
        canvas.drawText("Date: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(i.date))}", 400f, currentY, paint)
        currentY += 40f
        boldPaint.textSize = 14f
        canvas.drawText("Bill To:", 50f, currentY, boldPaint)
        currentY += 20f
        paint.textSize = 12f
        canvas.drawText(c.name, 50f, currentY, paint)
        currentY += 15f
        canvas.drawText("GSTIN: ${c.gstin ?: "N/A"}", 50f, currentY, paint)
        currentY += 15f
        canvas.drawText(c.address ?: "", 50f, currentY, paint)
        currentY += 40f
        paint.textSize = 12f
        canvas.drawText("Description", 50f, currentY, boldPaint)
        canvas.drawText("Qty", 300f, currentY, boldPaint)
        canvas.drawText("Rate", 380f, currentY, boldPaint)
        canvas.drawText("Total", 480f, currentY, boldPaint)
        currentY += 10f
        canvas.drawLine(50f, currentY, 550f, currentY, paint)
        currentY += 25f
        items.forEach { item ->
            canvas.drawText(item.itemName, 50f, currentY, paint)
            canvas.drawText("${item.quantity}", 300f, currentY, paint)
            canvas.drawText("${currencySymbol}${item.rate}", 380f, currentY, paint)
            canvas.drawText("${currencySymbol}${item.total}", 480f, currentY, paint)
            currentY += 20f
        }
        currentY += 20f
        canvas.drawLine(350f, currentY, 550f, currentY, paint)
        currentY += 25f
        canvas.drawText("Subtotal:", 350f, currentY, paint)
        canvas.drawText("${currencySymbol}${i.subTotal}", 480f, currentY, paint)
        currentY += 20f
        canvas.drawText("CGST:", 350f, currentY, paint)
        canvas.drawText("${currencySymbol}${i.cgst}", 480f, currentY, paint)
        currentY += 20f
        canvas.drawText("SGST:", 350f, currentY, paint)
        canvas.drawText("${currencySymbol}${i.sgst}", 480f, currentY, paint)
        currentY += 25f
        canvas.drawText("Grand Total:", 350f, currentY, boldPaint)
        canvas.drawText("${currencySymbol}${i.totalAmount}", 480f, currentY, boldPaint)
        currentY = 800f
        paint.textSize = 8f
        canvas.drawText("This app is a billing tool and does not provide tax or legal advice.", 50f, currentY, paint)
    }

    private fun drawModern(canvas: Canvas, paint: Paint, boldPaint: Paint, b: Business, c: Customer, i: Invoice, items: List<InvoiceItem>, currencySymbol: String) {
        canvas.drawColor(Color.WHITE)
        paint.color = Color.DKGRAY
        paint.textSize = 24f
        canvas.drawText(b.name.uppercase(), 50f, 60f, boldPaint)
        paint.textSize = 12f
        canvas.drawText("GSTIN: ${b.gstin}", 50f, 80f, paint)
        // Simple modern variation: colored header bar
        paint.color = Color.rgb(33, 150, 243)
        canvas.drawRect(0f, 100f, 595f, 110f, paint)
        drawClassic(canvas, paint.apply { color = Color.BLACK }, boldPaint, b, c, i, items, currencySymbol)
    }

    private fun drawThermal(canvas: Canvas, paint: Paint, boldPaint: Paint, b: Business, c: Customer, i: Invoice, items: List<InvoiceItem>, currencySymbol: String) {
        paint.textSize = 14f
        canvas.drawText(b.name, 10f, 30f, boldPaint)
        paint.textSize = 10f
        canvas.drawText(i.invoiceNumber, 10f, 50f, paint)
        canvas.drawLine(10f, 60f, 216f, 60f, paint)
        var y = 80f
        items.forEach { item ->
            canvas.drawText("${item.itemName} x ${item.quantity}", 10f, y, paint)
            y += 15f
            canvas.drawText("${currencySymbol}${item.total}", 180f, y-15f, paint)
        }
        canvas.drawLine(10f, y, 216f, y, paint)
        y += 20f
        canvas.drawText("Total: ${currencySymbol}${i.totalAmount}", 120f, y, boldPaint)
    }

    private fun drawWatermark(canvas: Canvas) {
        val watermarkPaint = Paint().apply {
            color = Color.LTGRAY
            alpha = 50
            textSize = 60f
            textAlign = Paint.Align.CENTER
        }
        canvas.save()
        canvas.rotate(-45f, 297f, 421f)
        canvas.drawText("MADE WITH INVOICE GEN", 297f, 421f, watermarkPaint)
        canvas.restore()
    }
}


