package com.example

import com.example.data.model.ProductEntity
import com.example.ui.viewmodel.CartItem
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testCartItemSubtotalCalculation() {
        val product = ProductEntity(
            id = 1,
            name = "BRAWN Amoxi-Clav",
            concentration = "1000 mg",
            dosageForm = "أقراص / Tablets",
            price = 65.0,
            stockQuantity = 100,
            totalSold = 20
        )
        val cartItem = CartItem(
            product = product,
            quantity = 5,
            unitPrice = product.price
        )
        assertEquals(325.0, cartItem.subtotal, 0.001)
    }

    @Test
    fun testTotalQuantitySum() {
        val items = listOf(
            CartItem(
                product = ProductEntity(id = 1, name = "P1", concentration = "10mg", dosageForm = "Tab", price = 10.0, stockQuantity = 50),
                quantity = 3,
                unitPrice = 10.0
            ),
            CartItem(
                product = ProductEntity(id = 2, name = "P2", concentration = "20mg", dosageForm = "Cap", price = 25.0, stockQuantity = 50),
                quantity = 4,
                unitPrice = 25.0
            )
        )
        val totalQty = items.sumOf { it.quantity }
        val totalAmount = items.sumOf { it.subtotal }
        assertEquals(7, totalQty)
        assertEquals(130.0, totalAmount, 0.001)
    }
}
