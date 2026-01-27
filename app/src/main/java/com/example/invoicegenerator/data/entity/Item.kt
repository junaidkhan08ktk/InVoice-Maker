package com.example.invoicegenerator.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val rate: Double,
    val unit: String = "Nos", // Nos, Pcs, Kg, etc.
    val hsnSac: String? = null,
    val gstRate: Double = 18.0 // Default GST rate for this item
)
