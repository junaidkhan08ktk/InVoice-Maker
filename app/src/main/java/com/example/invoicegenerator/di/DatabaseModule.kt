package com.example.invoicegenerator.di

import android.content.Context
import androidx.room.Room
import com.example.invoicegenerator.data.AppDatabase
import com.example.invoicegenerator.data.dao.BusinessDao
import com.example.invoicegenerator.data.dao.CustomerDao
import com.example.invoicegenerator.data.dao.InvoiceDao
import com.example.invoicegenerator.data.dao.ItemDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "invoice_db"
        ).build()
    }

    @Provides
    fun provideBusinessDao(database: AppDatabase): BusinessDao = database.businessDao()

    @Provides
    fun provideCustomerDao(database: AppDatabase): CustomerDao = database.customerDao()

    @Provides
    fun provideItemDao(database: AppDatabase): ItemDao = database.itemDao()

    @Provides
    fun provideInvoiceDao(database: AppDatabase): InvoiceDao = database.invoiceDao()
}
