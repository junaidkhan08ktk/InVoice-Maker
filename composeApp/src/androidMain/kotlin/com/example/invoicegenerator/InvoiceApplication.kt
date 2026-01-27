package com.example.invoicegenerator

import android.app.Application
import com.example.invoicegenerator.di.initKoin
import org.koin.android.ext.koin.androidContext

class InvoiceApplication : Application() {
    companion object {
        lateinit var instance: InvoiceApplication
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        initKoin {
            androidContext(this@InvoiceApplication)
        }
    }
}
