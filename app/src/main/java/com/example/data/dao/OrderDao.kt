package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemEntity
import com.example.data.model.OrderWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Transaction
    @Query("SELECT * FROM orders ORDER BY orderDate DESC")
    fun getAllOrdersWithItems(): Flow<List<OrderWithItems>>

    @Transaction
    @Query("SELECT * FROM orders WHERE repId = :repId ORDER BY orderDate DESC")
    fun getOrdersByRepWithItems(repId: Int): Flow<List<OrderWithItems>>

    @Transaction
    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    suspend fun getOrderWithItemsById(id: Int): OrderWithItems?

    @Transaction
    @Query("SELECT * FROM orders WHERE pharmacyId = :pharmacyId ORDER BY orderDate DESC")
    fun getOrdersByPharmacy(pharmacyId: Int): Flow<List<OrderWithItems>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Delete
    suspend fun deleteOrder(order: OrderEntity)

    @Query("DELETE FROM order_items WHERE orderId = :orderId")
    suspend fun deleteOrderItems(orderId: Int)

    @Query("SELECT COUNT(*) FROM orders")
    fun getTotalOrdersCount(): Flow<Int>

    @Query("SELECT SUM(totalAmount) FROM orders")
    fun getTotalSalesAmount(): Flow<Double?>

    @Query("SELECT SUM(totalQuantity) FROM orders")
    fun getTotalSoldQuantity(): Flow<Int?>
}
