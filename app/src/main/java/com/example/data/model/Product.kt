package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val concentration: String,
    val dosageForm: String, // Tablets, Capsules, Syrup, Injection, Ointment
    val price: Double,
    val stockQuantity: Int,
    val totalSold: Int = 0,
    val activeIngredient: String = "",
    val barcode: String = ""
)
