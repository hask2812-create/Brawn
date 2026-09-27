package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemEntity
import com.example.data.model.OrderWithItems
import com.example.data.model.PharmacyEntity
import com.example.data.model.ProductEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.data.repository.BrawnRepository
import com.example.ui.util.AppLanguage
import com.example.ui.util.LanguageManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class AppScreen {
    DASHBOARD,
    ORDERS,
    NEW_ORDER,
    PRODUCTS,
    PHARMACIES,
    REPORTS
}

enum class DateFilter {
    ALL,
    TODAY,
    THIS_WEEK,
    THIS_MONTH
}

data class CartItem(
    val product: ProductEntity,
    val quantity: Int,
    val unitPrice: Double
) {
    val subtotal: Double get() = quantity * unitPrice
}

class BrawnViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    val repository = BrawnRepository(database)

    // Current User & Auth
    private val _currentUser = MutableStateFlow<UserEntity>(
        UserEntity(
            id = 2,
            name = "د. عمر عبد الله",
            email = "omar@brawnpharma.com",
            phone = "0551234567",
            role = UserRole.SALES_REP,
            territory = "المنطقة الوسطى"
        )
    )
    val currentUser: StateFlow<UserEntity> = _currentUser.asStateFlow()

    // Language (Arabic default with toggle to English)
    private val _currentLanguage = MutableStateFlow(AppLanguage.ARABIC)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    // Active Screen
    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Data streams
    val allUsers = repository.allUsers.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allProducts = repository.allProducts.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val topSellingProducts = repository.topSellingProducts.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allPharmacies = repository.allPharmacies.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val topPharmacies = repository.topPharmacies.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allOrders = repository.allOrders.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Orders Filter State
    private val _ordersSearchQuery = MutableStateFlow("")
    val ordersSearchQuery: StateFlow<String> = _ordersSearchQuery.asStateFlow()

    private val _ordersDateFilter = MutableStateFlow(DateFilter.ALL)
    val ordersDateFilter: StateFlow<DateFilter> = _ordersDateFilter.asStateFlow()

    private val _ordersPharmacyFilter = MutableStateFlow<Int?>(null)
    val ordersPharmacyFilter: StateFlow<Int?> = _ordersPharmacyFilter.asStateFlow()

    // Filtered orders
    val filteredOrders: StateFlow<List<OrderWithItems>> = combine(
        allOrders,
        _ordersSearchQuery,
        _ordersDateFilter,
        _ordersPharmacyFilter,
        _currentUser
    ) { orders, query, dateFilter, pharmacyId, user ->
        val nowCal = Calendar.getInstance()
        val startOfToday = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val startOfWeek = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val startOfMonth = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        orders.filter { item ->
            // If rep, show only their orders unless admin
            val matchesUser = if (user.role == UserRole.ADMIN) true else item.order.repId == user.id

            val matchesQuery = query.isBlank() ||
                item.order.orderNumber.contains(query, ignoreCase = true) ||
                item.order.pharmacyName.contains(query, ignoreCase = true) ||
                item.items.any { it.productName.contains(query, ignoreCase = true) }

            val matchesDate = when (dateFilter) {
                DateFilter.ALL -> true
                DateFilter.TODAY -> item.order.orderDate >= startOfToday
                DateFilter.THIS_WEEK -> item.order.orderDate >= startOfWeek
                DateFilter.THIS_MONTH -> item.order.orderDate >= startOfMonth
            }

            val matchesPharmacy = pharmacyId == null || item.order.pharmacyId == pharmacyId

            matchesUser && matchesQuery && matchesDate && matchesPharmacy
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Products Search & Filter
    private val _productsSearchQuery = MutableStateFlow("")
    val productsSearchQuery: StateFlow<String> = _productsSearchQuery.asStateFlow()

    private val _productsDosageFilter = MutableStateFlow("الكل")
    val productsDosageFilter: StateFlow<String> = _productsDosageFilter.asStateFlow()

    val filteredProducts: StateFlow<List<ProductEntity>> = combine(
        allProducts,
        _productsSearchQuery,
        _productsDosageFilter
    ) { products, query, dosage ->
        products.filter { product ->
            val matchesQuery = query.isBlank() ||
                product.name.contains(query, ignoreCase = true) ||
                product.concentration.contains(query, ignoreCase = true) ||
                product.activeIngredient.contains(query, ignoreCase = true)

            val matchesDosage = dosage == "الكل" || dosage == "All" || product.dosageForm.contains(dosage, ignoreCase = true)

            matchesQuery && matchesDosage
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Pharmacies Search
    private val _pharmaciesSearchQuery = MutableStateFlow("")
    val pharmaciesSearchQuery: StateFlow<String> = _pharmaciesSearchQuery.asStateFlow()

    val filteredPharmacies: StateFlow<List<PharmacyEntity>> = combine(
        allPharmacies,
        _pharmaciesSearchQuery
    ) { list, query ->
        list.filter { pharmacy ->
            query.isBlank() ||
                pharmacy.name.contains(query, ignoreCase = true) ||
                pharmacy.pharmacistName.contains(query, ignoreCase = true) ||
                pharmacy.address.contains(query, ignoreCase = true) ||
                pharmacy.phone.contains(query)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- New Order Cart State ---
    private val _selectedPharmacy = MutableStateFlow<PharmacyEntity?>(null)
    val selectedPharmacy: StateFlow<PharmacyEntity?> = _selectedPharmacy.asStateFlow()

    private val _cartItems = MutableStateFlow<Map<Int, CartItem>>(emptyMap())
    val cartItems: StateFlow<Map<Int, CartItem>> = _cartItems.asStateFlow()

    private val _orderPaymentStatus = MutableStateFlow("نقداً / Cash")
    val orderPaymentStatus: StateFlow<String> = _orderPaymentStatus.asStateFlow()

    private val _orderNotes = MutableStateFlow("")
    val orderNotes: StateFlow<String> = _orderNotes.asStateFlow()

    val cartTotalAmount: StateFlow<Double> = combine(_cartItems) { mapList ->
        mapList[0].values.sumOf { it.subtotal }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartTotalQuantity: StateFlow<Int> = combine(_cartItems) { mapList ->
        mapList[0].values.sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Dialogs & Modals
    private val _productInEdit = MutableStateFlow<ProductEntity?>(null)
    val productInEdit: StateFlow<ProductEntity?> = _productInEdit.asStateFlow()
    val isAddProductOpen = MutableStateFlow(false)

    private val _pharmacyInEdit = MutableStateFlow<PharmacyEntity?>(null)
    val pharmacyInEdit: StateFlow<PharmacyEntity?> = _pharmacyInEdit.asStateFlow()
    val isAddPharmacyOpen = MutableStateFlow(false)

    private val _selectedOrderDetails = MutableStateFlow<OrderWithItems?>(null)
    val selectedOrderDetails: StateFlow<OrderWithItems?> = _selectedOrderDetails.asStateFlow()

    val isUserSwitchOpen = MutableStateFlow(false)

    // --- Actions ---

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun toggleLanguage() {
        _currentLanguage.value = if (_currentLanguage.value == AppLanguage.ARABIC) {
            AppLanguage.ENGLISH
        } else {
            AppLanguage.ARABIC
        }
    }

    fun switchUser(user: UserEntity) {
        _currentUser.value = user
    }

    // New Order Cart Actions
    fun selectPharmacy(pharmacy: PharmacyEntity?) {
        _selectedPharmacy.value = pharmacy
    }

    fun setPaymentStatus(status: String) {
        _orderPaymentStatus.value = status
    }

    fun setOrderNotes(notes: String) {
        _orderNotes.value = notes
    }

    fun addToCart(product: ProductEntity, quantity: Int = 1) {
        val current = _cartItems.value.toMutableMap()
        val existing = current[product.id]
        val newQty = (existing?.quantity ?: 0) + quantity
        if (newQty > 0) {
            current[product.id] = CartItem(
                product = product,
                quantity = newQty,
                unitPrice = product.price
            )
        } else {
            current.remove(product.id)
        }
        _cartItems.value = current
    }

    fun updateCartItemQuantity(productId: Int, newQuantity: Int) {
        val current = _cartItems.value.toMutableMap()
        val existing = current[productId] ?: return
        if (newQuantity > 0) {
            current[productId] = existing.copy(quantity = newQuantity)
        } else {
            current.remove(productId)
        }
        _cartItems.value = current
    }

    fun removeFromCart(productId: Int) {
        val current = _cartItems.value.toMutableMap()
        current.remove(productId)
        _cartItems.value = current
    }

    fun clearCart() {
        _cartItems.value = emptyMap()
        _selectedPharmacy.value = null
        _orderNotes.value = ""
        _orderPaymentStatus.value = "نقداً / Cash"
    }

    fun submitOrder(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val pharmacy = _selectedPharmacy.value
        if (pharmacy == null) {
            onError(if (_currentLanguage.value == AppLanguage.ARABIC) "يرجى اختيار الصيدلية أولاً" else "Please select a pharmacy first")
            return
        }

        val items = _cartItems.value.values.toList()
        if (items.isEmpty()) {
            onError(if (_currentLanguage.value == AppLanguage.ARABIC) "يرجى إضافة منتج واحد على الأقل للطلب" else "Please add at least one product")
            return
        }

        viewModelScope.launch {
            try {
                val orderNumber = "BRW-${System.currentTimeMillis().toString().takeLast(6)}"
                val totalQty = items.sumOf { it.quantity }
                val totalAmt = items.sumOf { it.subtotal }

                val order = OrderEntity(
                    orderNumber = orderNumber,
                    pharmacyId = pharmacy.id,
                    pharmacyName = pharmacy.name,
                    repId = _currentUser.value.id,
                    repName = _currentUser.value.name,
                    orderDate = System.currentTimeMillis(),
                    totalAmount = totalAmt,
                    totalQuantity = totalQty,
                    paymentStatus = _orderPaymentStatus.value,
                    notes = _orderNotes.value
                )

                val orderItems = items.map { cartItem ->
                    OrderItemEntity(
                        orderId = 0, // Assigned in transaction
                        productId = cartItem.product.id,
                        productName = cartItem.product.name,
                        concentration = cartItem.product.concentration,
                        dosageForm = cartItem.product.dosageForm,
                        unitPrice = cartItem.unitPrice,
                        quantity = cartItem.quantity,
                        subtotal = cartItem.subtotal
                    )
                }

                repository.placeOrder(order, orderItems)
                clearCart()
                _currentScreen.value = AppScreen.ORDERS
                onSuccess(orderNumber)
            } catch (e: Exception) {
                onError(e.message ?: "Failed to place order")
            }
        }
    }

    // Search & Filter setters
    fun setOrdersSearch(query: String) { _ordersSearchQuery.value = query }
    fun setOrdersDateFilter(filter: DateFilter) { _ordersDateFilter.value = filter }
    fun setOrdersPharmacyFilter(pharmacyId: Int?) { _ordersPharmacyFilter.value = pharmacyId }

    fun setProductsSearch(query: String) { _productsSearchQuery.value = query }
    fun setProductsDosageFilter(dosage: String) { _productsDosageFilter.value = dosage }

    fun setPharmaciesSearch(query: String) { _pharmaciesSearchQuery.value = query }

    // Dialog Triggers
    fun openAddProduct() {
        _productInEdit.value = null
        isAddProductOpen.value = true
    }

    fun openEditProduct(product: ProductEntity) {
        _productInEdit.value = product
        isAddProductOpen.value = true
    }

    fun closeProductDialog() {
        _productInEdit.value = null
        isAddProductOpen.value = false
    }

    fun saveProduct(
        name: String,
        concentration: String,
        dosageForm: String,
        price: Double,
        stock: Int,
        activeIngredient: String
    ) {
        viewModelScope.launch {
            val current = _productInEdit.value
            if (current == null) {
                repository.insertProduct(
                    ProductEntity(
                        name = name,
                        concentration = concentration,
                        dosageForm = dosageForm,
                        price = price,
                        stockQuantity = stock,
                        totalSold = 0,
                        activeIngredient = activeIngredient
                    )
                )
            } else {
                repository.updateProduct(
                    current.copy(
                        name = name,
                        concentration = concentration,
                        dosageForm = dosageForm,
                        price = price,
                        stockQuantity = stock,
                        activeIngredient = activeIngredient
                    )
                )
            }
            closeProductDialog()
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    fun openAddPharmacy() {
        _pharmacyInEdit.value = null
        isAddPharmacyOpen.value = true
    }

    fun openEditPharmacy(pharmacy: PharmacyEntity) {
        _pharmacyInEdit.value = pharmacy
        isAddPharmacyOpen.value = true
    }

    fun closePharmacyDialog() {
        _pharmacyInEdit.value = null
        isAddPharmacyOpen.value = false
    }

    fun savePharmacy(
        name: String,
        pharmacistName: String,
        phone: String,
        address: String,
        notes: String
    ) {
        viewModelScope.launch {
            val current = _pharmacyInEdit.value
            if (current == null) {
                repository.insertPharmacy(
                    PharmacyEntity(
                        name = name,
                        pharmacistName = pharmacistName,
                        phone = phone,
                        address = address,
                        notes = notes,
                        assignedRepId = _currentUser.value.id
                    )
                )
            } else {
                repository.updatePharmacy(
                    current.copy(
                        name = name,
                        pharmacistName = pharmacistName,
                        phone = phone,
                        address = address,
                        notes = notes
                    )
                )
            }
            closePharmacyDialog()
        }
    }

    fun deletePharmacy(pharmacy: PharmacyEntity) {
        viewModelScope.launch {
            repository.deletePharmacy(pharmacy)
        }
    }

    fun openOrderDetails(orderWithItems: OrderWithItems) {
        _selectedOrderDetails.value = orderWithItems
    }

    fun closeOrderDetails() {
        _selectedOrderDetails.value = null
    }

    fun exportCsvReport(context: Context) {
        val list = filteredOrders.value
        LanguageManager.exportOrdersToCsv(context, list, _currentLanguage.value)
    }
}
