package com.example.invoicegenerator.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "invoice_items",
    foreignKeys = [
        ForeignKey(
            entity = Invoice::class,
            parentColumns = ["id"],
            childColumns = ["invoiceId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class InvoiceItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val invoiceId: Long,
    val itemId: Long?, // Nullable if it's a one-off item
    val itemName: String,
    val quantity: Double,
    val rate: Double,
    val gstRate: Double,
    val cgst: Double,
    val sgst: Double,
    val igst: Double,
    val total: Double
)
