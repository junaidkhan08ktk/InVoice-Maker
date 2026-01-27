package com.example.invoicegenerator.di

import com.example.invoicegenerator.data.createAndroidDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { createAndroidDataStore(get()) }
}
