package com.example.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = PharmacyEntity::class,
            parentColumns = ["id"],
            childColumns = ["pharmacyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["pharmacyId"])]
)
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val orderNumber: String,
    val pharmacyId: Int,
    val pharmacyName: String,
    val repId: Int,
    val repName: String,
    val orderDate: Long = System.currentTimeMillis(),
    val totalAmount: Double,
    val totalQuantity: Int,
    val paymentStatus: String = "نقداً / Cash", // "نقداً / Cash", "آجل / Credit"
    val notes: String = ""
)

@Entity(
    tableName = "order_items",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["orderId"])]
)
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val orderId: Int,
    val productId: Int,
    val productName: String,
    val concentration: String,
    val dosageForm: String,
    val unitPrice: Double,
    val quantity: Int,
    val subtotal: Double
)

data class OrderWithItems(
    @Embedded val order: OrderEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "orderId"
    )
    val items: List<OrderItemEntity>
)
