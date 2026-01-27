package com.example.invoicegenerator.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.invoicegenerator.data.dao.*
import com.example.invoicegenerator.data.entity.*

@Database(
    entities = [
        Business::class,
        Customer::class,
        Item::class,
        Invoice::class,
        InvoiceItem::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun businessDao(): BusinessDao
    abstract fun customerDao(): CustomerDao
    abstract fun itemDao(): ItemDao
    abstract fun invoiceDao(): InvoiceDao
}
