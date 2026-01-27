package com.example.invoicegenerator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.invoicegenerator.data.dao.InvoiceDao
import com.example.invoicegenerator.data.entity.Invoice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val invoiceDao: InvoiceDao
) : ViewModel() {

    private val _stats = MutableStateFlow(DashboardStats())
    val stats: StateFlow<DashboardStats> = _stats.asStateFlow()

    init {
        viewModelScope.launch {
            invoiceDao.getAllInvoices().collect { invoices ->
                val totalSales = invoices.sumOf { it.totalAmount }
                val paidCount = invoices.count { it.isPaid }
                val unpaidCount = invoices.count { !it.isPaid }
                _stats.value = DashboardStats(
                    totalSales = totalSales,
                    paidInvoicesCount = paidCount,
                    unpaidInvoicesCount = unpaidCount,
                    recentInvoices = invoices.take(5),
                    allInvoices = invoices
                )
            }
        }
    }
}

data class DashboardStats(
    val totalSales: Double = 0.0,
    val paidInvoicesCount: Int = 0,
    val unpaidInvoicesCount: Int = 0,
    val recentInvoices: List<Invoice> = emptyList(),
    val allInvoices: List<Invoice> = emptyList()
)
