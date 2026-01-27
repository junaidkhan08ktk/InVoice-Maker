package com.example.invoicegenerator.di

import com.example.invoicegenerator.data.createIosDataStore
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { createIosDataStore() }
}
