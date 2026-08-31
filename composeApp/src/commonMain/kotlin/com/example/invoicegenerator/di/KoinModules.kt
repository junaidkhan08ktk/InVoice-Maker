package com.example.invoicegenerator.di

import com.example.invoicegenerator.data.AppDatabase
import com.example.invoicegenerator.data.getDatabaseBuilder
import com.example.invoicegenerator.data.getRoomDatabase
import com.example.invoicegenerator.viewmodel.BusinessViewModel
import com.example.invoicegenerator.viewmodel.DashboardViewModel
import com.example.invoicegenerator.viewmodel.InvoiceViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.core.KoinApplication

val appModule = module {
    single { getRoomDatabase(getDatabaseBuilder()) }
    single { get<AppDatabase>().businessDao() }
    single { get<AppDatabase>().customerDao() }
    single { get<AppDatabase>().itemDao() }
    single { get<AppDatabase>().invoiceDao() }
    
    // Preferences
    single { com.example.invoicegenerator.data.PreferencesManager(get()) }
}

val viewModelModule = module {
    viewModel { BusinessViewModel(get()) }
    viewModel { DashboardViewModel(get(), get(), get(), get()) }
    viewModel { InvoiceViewModel(get(), get(), get(), get()) }
    viewModel { com.example.invoicegenerator.viewmodel.SettingsViewModel(get()) }
}


fun initKoin(config: (KoinApplication.() -> Unit)? = null) {
    org.koin.core.context.startKoin {
        config?.invoke(this)
        modules(platformModule, appModule, viewModelModule)
    }
}

expect val platformModule: Module
