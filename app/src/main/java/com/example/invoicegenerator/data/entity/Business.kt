package com.example.invoicegenerator.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "business_profile")
data class Business(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val gstin: String,
    val address: String,
    val email: String? = null,
    val phone: String? = null,
    val logoUri: String? = null,
    val defaultGstRate: Double = 18.0
)
