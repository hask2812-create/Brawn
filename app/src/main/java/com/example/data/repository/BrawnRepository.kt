package com.example.data.repository

import androidx.room.withTransaction
import com.example.data.database.AppDatabase
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemEntity
import com.example.data.model.OrderWithItems
import com.example.data.model.PharmacyEntity
import com.example.data.model.ProductEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

class BrawnRepository(private val database: AppDatabase) {
    private val userDao = database.userDao()
    private val productDao = database.productDao()
    private val pharmacyDao = database.pharmacyDao()
    private val orderDao = database.orderDao()

    // Users
    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsers()
    suspend fun insertUser(user: UserEntity): Long = userDao.insertUser(user)
    suspend fun updateUser(user: UserEntity) = userDao.updateUser(user)

    // Products
    val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()
    val topSellingProducts: Flow<List<ProductEntity>> = productDao.getTopSellingProducts(5)
    fun searchProducts(query: String): Flow<List<ProductEntity>> = productDao.searchProducts(query)
    suspend fun insertProduct(product: ProductEntity): Long = productDao.insertProduct(product)
    suspend fun updateProduct(product: ProductEntity) = productDao.updateProduct(product)
    suspend fun deleteProduct(product: ProductEntity) = productDao.deleteProduct(product)

    // Pharmacies
    val allPharmacies: Flow<List<PharmacyEntity>> = pharmacyDao.getAllPharmacies()
    val topPharmacies: Flow<List<PharmacyEntity>> = pharmacyDao.getTopPharmacies(5)
    fun searchPharmacies(query: String): Flow<List<PharmacyEntity>> = pharmacyDao.searchPharmacies(query)
    suspend fun insertPharmacy(pharmacy: PharmacyEntity): Long = pharmacyDao.insertPharmacy(pharmacy)
    suspend fun updatePharmacy(pharmacy: PharmacyEntity) = pharmacyDao.updatePharmacy(pharmacy)
    suspend fun deletePharmacy(pharmacy: PharmacyEntity) = pharmacyDao.deletePharmacy(pharmacy)

    // Orders
    val allOrders: Flow<List<OrderWithItems>> = orderDao.getAllOrdersWithItems()
    fun getOrdersByRep(repId: Int): Flow<List<OrderWithItems>> = orderDao.getOrdersByRepWithItems(repId)
    val totalOrdersCount: Flow<Int> = orderDao.getTotalOrdersCount()
    val totalSalesAmount: Flow<Double?> = orderDao.getTotalSalesAmount()
    val totalSoldQuantity: Flow<Int?> = orderDao.getTotalSoldQuantity()

    // Transactional Order Placement
    suspend fun placeOrder(
        order: OrderEntity,
        items: List<OrderItemEntity>
    ): Long {
        return database.withTransaction {
            val orderId = orderDao.insertOrder(order)
            val itemsWithOrderId = items.map { it.copy(orderId = orderId.toInt()) }
            orderDao.insertOrderItems(itemsWithOrderId)

            // Update inventory & sales count for each product
            for (item in items) {
                productDao.updateStockAndSold(item.productId, item.quantity)
            }

            // Update pharmacy total purchases and last order date
            pharmacyDao.updatePurchasesAndLastOrder(
                pharmacyId = order.pharmacyId,
                amount = order.totalAmount,
                orderDate = order.orderDate
            )

            orderId
        }
    }

    suspend fun deleteOrder(orderWithItems: OrderWithItems) {
        database.withTransaction {
            orderDao.deleteOrderItems(orderWithItems.order.id)
            orderDao.deleteOrder(orderWithItems.order)
        }
    }
}
