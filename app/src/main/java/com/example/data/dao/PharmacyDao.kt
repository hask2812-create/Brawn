package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PharmacyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PharmacyDao {
    @Query("SELECT * FROM pharmacies ORDER BY name ASC")
    fun getAllPharmacies(): Flow<List<PharmacyEntity>>

    @Query("SELECT * FROM pharmacies WHERE id = :id LIMIT 1")
    suspend fun getPharmacyById(id: Int): PharmacyEntity?

    @Query("SELECT * FROM pharmacies WHERE name LIKE '%' || :query || '%' OR pharmacistName LIKE '%' || :query || '%' OR address LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchPharmacies(query: String): Flow<List<PharmacyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPharmacy(pharmacy: PharmacyEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPharmacies(pharmacies: List<PharmacyEntity>)

    @Update
    suspend fun updatePharmacy(pharmacy: PharmacyEntity)

    @Delete
    suspend fun deletePharmacy(pharmacy: PharmacyEntity)

    @Query("UPDATE pharmacies SET totalPurchases = totalPurchases + :amount, lastOrderDate = :orderDate WHERE id = :pharmacyId")
    suspend fun updatePurchasesAndLastOrder(pharmacyId: Int, amount: Double, orderDate: Long)

    @Query("SELECT * FROM pharmacies ORDER BY totalPurchases DESC LIMIT :limit")
    fun getTopPharmacies(limit: Int = 5): Flow<List<PharmacyEntity>>
}
