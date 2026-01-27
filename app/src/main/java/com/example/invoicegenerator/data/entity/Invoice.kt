package com.example.invoicegenerator.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "invoices")
data class Invoice(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val invoiceNumber: String,
    val date: Long = System.currentTimeMillis(),
    val customerId: Long,
    val businessId: Long,
    val subTotal: Double,
    val cgst: Double,
    val sgst: Double,
    val igst: Double,
    val totalAmount: Double,
    val discount: Double = 0.0,
    val isPaid: Boolean = false,
    val notes: String? = null
)
