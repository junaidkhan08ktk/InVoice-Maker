package com.example.invoicegenerator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.invoicegenerator.data.dao.BusinessDao
import com.example.invoicegenerator.data.dao.CustomerDao
import com.example.invoicegenerator.data.dao.InvoiceDao
import com.example.invoicegenerator.data.dao.ItemDao
import com.example.invoicegenerator.data.entity.Business
import com.example.invoicegenerator.data.entity.Customer
import com.example.invoicegenerator.data.entity.Invoice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class DashboardViewModel(
    private val invoiceDao: InvoiceDao,
    private val customerDao: CustomerDao,
    private val itemDao: ItemDao,
    private val businessDao: BusinessDao
) : ViewModel() {

    private val _stats = MutableStateFlow(DashboardStats())
    val stats: StateFlow<DashboardStats> = _stats.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                invoiceDao.getAllInvoices(),
                customerDao.getAllCustomers(),
                itemDao.getAllItems(),
                businessDao.getBusinessProfile()
            ) { invoices, customers, items, business ->
                val customerMap = customers.associateBy { it.id }
                val totalSales = invoices.sumOf { it.totalAmount }
                val paidInvoices = invoices.filter { it.isPaid }
                val unpaidInvoices = invoices.filter { !it.isPaid }

                val invoicesWithCustomers = invoices.map { inv ->
                    InvoiceWithCustomer(
                        invoice = inv,
                        customer = customerMap[inv.customerId]
                    )
                }

                val weekly = calculateWeeklyRevenue(invoices)

                DashboardStats(
                    totalSales = totalSales,
                    paidInvoicesCount = paidInvoices.size,
                    paidInvoicesAmount = paidInvoices.sumOf { it.totalAmount },
                    unpaidInvoicesCount = unpaidInvoices.size,
                    unpaidInvoicesAmount = unpaidInvoices.sumOf { it.totalAmount },
                    totalCustomersCount = customers.size,
                    totalItemsCount = items.size,
                    recentInvoices = invoicesWithCustomers.take(5),
                    allInvoices = invoicesWithCustomers,
                    weeklyRevenue = weekly,
                    businessProfile = business
                )
            }.collect {
                _stats.value = it
            }
        }
    }

    private fun calculateWeeklyRevenue(invoices: List<Invoice>): List<DayRevenue> {
        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val daySums = mutableMapOf<String, Double>()
        days.forEach { daySums[it] = 0.0 }

        for (invoice in invoices) {
            try {
                val instant = Instant.fromEpochMilliseconds(invoice.date)
                val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
                val dayOfWeek = when (dateTime.dayOfWeek.name) {
                    "MONDAY" -> "Mon"
                    "TUESDAY" -> "Tue"
                    "WEDNESDAY" -> "Wed"
                    "THURSDAY" -> "Thu"
                    "FRIDAY" -> "Fri"
                    "SATURDAY" -> "Sat"
                    "SUNDAY" -> "Sun"
                    else -> "Mon"
                }
                daySums[dayOfWeek] = (daySums[dayOfWeek] ?: 0.0) + invoice.totalAmount
            } catch (e: Exception) {
                // Ignore parse errors
            }
        }

        return days.map { DayRevenue(day = it, amount = daySums[it] ?: 0.0) }
    }

    fun toggleInvoiceStatus(invoice: Invoice) {
        viewModelScope.launch {
            invoiceDao.updateInvoice(invoice.copy(isPaid = !invoice.isPaid))
        }
    }

    fun deleteInvoice(invoice: Invoice) {
        viewModelScope.launch {
            invoiceDao.deleteInvoice(invoice)
        }
    }
}

data class DashboardStats(
    val totalSales: Double = 0.0,
    val paidInvoicesCount: Int = 0,
    val paidInvoicesAmount: Double = 0.0,
    val unpaidInvoicesCount: Int = 0,
    val unpaidInvoicesAmount: Double = 0.0,
    val totalCustomersCount: Int = 0,
    val totalItemsCount: Int = 0,
    val recentInvoices: List<InvoiceWithCustomer> = emptyList(),
    val allInvoices: List<InvoiceWithCustomer> = emptyList(),
    val weeklyRevenue: List<DayRevenue> = emptyList(),
    val businessProfile: Business? = null
)

data class InvoiceWithCustomer(
    val invoice: Invoice,
    val customer: Customer?
)

data class DayRevenue(
    val day: String,
    val amount: Double
)

