package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pharmacies")
data class PharmacyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val pharmacistName: String,
    val phone: String,
    val address: String,
    val totalPurchases: Double = 0.0,
    val lastOrderDate: Long? = null,
    val assignedRepId: Int = 1,
    val notes: String = ""
)
