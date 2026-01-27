package com.example.invoicegenerator.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val appContext = com.example.invoicegenerator.InvoiceApplication.instance.applicationContext
    val dbFile = appContext.getDatabasePath("invoice.db")
    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}
