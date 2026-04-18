package com.example.invoicegenerator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.invoicegenerator.data.dao.CustomerDao
import com.example.invoicegenerator.data.dao.InvoiceDao
import com.example.invoicegenerator.data.dao.ItemDao
import com.example.invoicegenerator.data.entity.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class InvoiceViewModel(
    private val invoiceDao: InvoiceDao,
    private val customerDao: CustomerDao,
    private val itemDao: ItemDao,
    private val preferencesManager: com.example.invoicegenerator.data.PreferencesManager
) : ViewModel() {

    private val _customers = MutableStateFlow<List<Customer>>(emptyList())
    val customers = _customers.asStateFlow()

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items = _items.asStateFlow()

    private val _selectedCustomer = MutableStateFlow<Customer?>(null)
    val selectedCustomer = _selectedCustomer.asStateFlow()

    private val _invoiceItems = MutableStateFlow<List<InvoiceItemState>>(emptyList())
    val invoiceItems = _invoiceItems.asStateFlow()

    init {
        viewModelScope.launch {
            customerDao.getAllCustomers().collect { _customers.value = it }
        }
        viewModelScope.launch {
            itemDao.getAllItems().collect { _items.value = it }
        }
    }

    fun selectCustomer(customer: Customer) {
        _selectedCustomer.value = customer
    }

    fun addInvoiceItem(item: Item, quantity: Double = 1.0) {
        val newItem = InvoiceItemState(
            itemId = item.id,
            itemName = item.name,
            quantity = quantity,
            rate = item.rate,
            gstRate = item.gstRate
        )
        _invoiceItems.value = _invoiceItems.value + newItem
    }

    fun updateItemQuantity(index: Int, quantity: Double) {
        val current = _invoiceItems.value.toMutableList()
        current[index] = current[index].copy(quantity = quantity)
        _invoiceItems.value = current
    }

    fun removeItem(index: Int) {
        val current = _invoiceItems.value.toMutableList()
        current.removeAt(index)
        _invoiceItems.value = current
    }

    fun calculateTotals(businessProfile: Business?): InvoiceTotals {
        val subTotal = _invoiceItems.value.sumOf { it.rate * it.quantity }
        var totalCgst = 0.0
        var totalSgst = 0.0
        var totalIgst = 0.0

        _invoiceItems.value.forEach { item ->
            val itemTotal = item.rate * item.quantity
            val cgst = (itemTotal * (item.gstRate / 2)) / 100
            val sgst = (itemTotal * (item.gstRate / 2)) / 100
            totalCgst += cgst
            totalSgst += sgst
        }

        return InvoiceTotals(
            subTotal = subTotal,
            cgst = totalCgst,
            sgst = totalSgst,
            igst = totalIgst,
            total = subTotal + totalCgst + totalSgst + totalIgst
        )
    }

    fun saveInvoice(businessId: Long, invoiceNumber: String, onComplete: (Long) -> Unit) {
        val customer = _selectedCustomer.value ?: return
        val totals = calculateTotals(null)

        viewModelScope.launch {
            val invoiceId = invoiceDao.insertInvoice(
                Invoice(
                    invoiceNumber = invoiceNumber,
                    customerId = customer.id,
                    businessId = businessId,
                    subTotal = totals.subTotal,
                    cgst = totals.cgst,
                    sgst = totals.sgst,
                    igst = totals.igst,
                    totalAmount = totals.total
                )
            )

            val itemsToSave = _invoiceItems.value.map { state ->
                InvoiceItem(
                    invoiceId = invoiceId,
                    itemId = state.itemId,
                    itemName = state.itemName,
                    quantity = state.quantity,
                    rate = state.rate,
                    gstRate = state.gstRate,
                    cgst = (state.rate * state.quantity * (state.gstRate / 2)) / 100,
                    sgst = (state.rate * state.quantity * (state.gstRate / 2)) / 100,
                    igst = 0.0,
                    total = (state.rate * state.quantity) * (1 + state.gstRate / 100)
                )
            }
            invoiceDao.insertInvoiceItems(itemsToSave)
            onComplete(invoiceId)
        }
    }

    fun addCustomer(name: String, gstin: String?, address: String?) {
        viewModelScope.launch {
            customerDao.insertCustomer(Customer(name = name, gstin = gstin, address = address))
        }
    }

    fun addItem(name: String, rate: Double, gstRate: Double) {
        viewModelScope.launch {
            itemDao.insertItem(Item(name = name, rate = rate, gstRate = gstRate))
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            itemDao.deleteItem(item)
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            customerDao.deleteCustomer(customer)
        }
    }

    fun getInvoiceById(id: Long) = flow {
        if (id == -1L) {
            emit(getSampleInvoice())
        } else {
            val invoice = invoiceDao.getInvoiceById(id)
            if (invoice != null) {
                val customer = customerDao.getCustomerById(invoice.customerId)
                invoiceDao.getItemsForInvoice(id).collect { items ->
                    emit(InvoiceWithItems(invoice, items, customer))
                }
            }
        }
    }

    private fun getSampleInvoice(): InvoiceWithItems {
        val sampleInvoice = Invoice(
            invoiceNumber = "SAMPLE-001",
            customerId = 0,
            businessId = 0,
            subTotal = 1000.0,
            cgst = 90.0,
            sgst = 90.0,
            igst = 0.0,
            totalAmount = 1180.0,
            notes = "This is a sample invoice."
        )
        val sampleItems = listOf(
            InvoiceItem(
                invoiceId = 0,
                itemId = 0,
                itemName = "Design Services",
                quantity = 1.0,
                rate = 1000.0,
                gstRate = 18.0,
                cgst = 90.0,
                sgst = 90.0,
                igst = 0.0,
                total = 1180.0
            )
        )
        val sampleCustomer = Customer(name = "junaid Developer", email = "test@sample.com")
        return InvoiceWithItems(sampleInvoice, sampleItems, sampleCustomer)
    }
}

data class InvoiceWithItems(
    val invoice: Invoice,
    val items: List<InvoiceItem>,
    val customer: Customer?
)

data class InvoiceItemState(
    val itemId: Long? = null,
    val itemName: String = "",
    val quantity: Double = 1.0,
    val rate: Double = 0.0,
    val gstRate: Double = 18.0
)

data class InvoiceTotals(
    val subTotal: Double = 0.0,
    val cgst: Double = 0.0,
    val sgst: Double = 0.0,
    val igst: Double = 0.0,
    val total: Double = 0.0
)
