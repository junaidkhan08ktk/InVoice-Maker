package com.example.invoicegenerator.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val gstin: String? = null,
    val address: String? = null,
    val phone: String? = null,
    val email: String? = null
)
