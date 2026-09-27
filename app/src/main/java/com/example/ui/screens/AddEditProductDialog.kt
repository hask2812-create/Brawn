package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProductEntity
import com.example.ui.theme.BrawnNavyPrimary
import com.example.ui.util.AppLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductDialog(
    product: ProductEntity?,
    language: AppLanguage,
    onSave: (name: String, concentration: String, dosage: String, price: Double, stock: Int, activeIngredient: String) -> Unit,
    onDismiss: () -> Unit
) {
    val isArabic = language == AppLanguage.ARABIC
    val isEditing = product != null

    var name by remember { mutableStateOf(product?.name ?: "") }
    var concentration by remember { mutableStateOf(product?.concentration ?: "") }
    var dosageForm by remember { mutableStateOf(product?.dosageForm ?: "أقراص / Tablets") }
    var priceText by remember { mutableStateOf(product?.price?.toString() ?: "") }
    var stockText by remember { mutableStateOf(product?.stockQuantity?.toString() ?: "") }
    var activeIngredient by remember { mutableStateOf(product?.activeIngredient ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val dosageOptions = listOf(
        "أقراص / Tablets",
        "كبسولات / Capsules",
        "كبسولات جيلاتينية / Softgels",
        "شراب / Syrup",
        "أمبولات / Injections",
        "مرهم / Gel",
        "قطرة / Drops"
    )
    var dosageExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEditing) {
                    if (isArabic) "تعديل بيانات المنتج الدوائي" else "Edit Pharmaceutical Product"
                } else {
                    if (isArabic) "إضافة دواء جديد لشركة BRAWN" else "Add New BRAWN Product"
                },
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = BrawnNavyPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorMessage = null },
                    label = { Text(if (isArabic) "اسم المنتج التجاري *" else "Product Name *") },
                    placeholder = { Text("e.g. BRAWN Amoxi-Clav") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("product_name_input")
                )

                OutlinedTextField(
                    value = concentration,
                    onValueChange = { concentration = it; errorMessage = null },
                    label = { Text(if (isArabic) "التركيز *" else "Strength / Concentration *") },
                    placeholder = { Text("e.g. 1000 mg, 500 mg, 20 mg") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("product_concentration_input")
                )

                // Dosage Form Dropdown
                ExposedDropdownMenuBox(
                    expanded = dosageExpanded,
                    onExpandedChange = { dosageExpanded = !dosageExpanded }
                ) {
                    OutlinedTextField(
                        value = dosageForm,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(if (isArabic) "الشكل الدوائي *" else "Dosage Form *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dosageExpanded) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = dosageExpanded,
                        onDismissRequest = { dosageExpanded = false }
                    ) {
                        dosageOptions.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt) },
                                onClick = {
                                    dosageForm = opt
                                    dosageExpanded = false
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it; errorMessage = null },
                        label = { Text(if (isArabic) "السعر (ر.س) *" else "Price (SAR) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("product_price_input")
                    )

                    OutlinedTextField(
                        value = stockText,
                        onValueChange = { stockText = it; errorMessage = null },
                        label = { Text(if (isArabic) "المخزون المتوفر *" else "Stock Quantity *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("product_stock_input")
                    )
                }

                OutlinedTextField(
                    value = activeIngredient,
                    onValueChange = { activeIngredient = it },
                    label = { Text(if (isArabic) "المادة الفعالة" else "Active Ingredient") },
                    placeholder = { Text("e.g. Amoxicillin + Clavulanic") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorMessage = if (isArabic) "يرجى إدخال اسم المنتج" else "Please enter product name"
                        return@Button
                    }
                    if (concentration.isBlank()) {
                        errorMessage = if (isArabic) "يرجى إدخال التركيز" else "Please enter concentration"
                        return@Button
                    }
                    val price = priceText.toDoubleOrNull()
                    if (price == null || price <= 0) {
                        errorMessage = if (isArabic) "يرجى إدخال سعر صالح أكبر من صفر" else "Please enter valid price"
                        return@Button
                    }
                    val stock = stockText.toIntOrNull()
                    if (stock == null || stock < 0) {
                        errorMessage = if (isArabic) "يرجى إدخال كمية مخزون صحيحة" else "Please enter valid stock"
                        return@Button
                    }

                    onSave(name.trim(), concentration.trim(), dosageForm, price, stock, activeIngredient.trim())
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrawnNavyPrimary),
                modifier = Modifier.testTag("save_product_confirm_button")
            ) {
                Text(if (isArabic) "حفظ المنتج" else "Save Product")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isArabic) "إلغاء" else "Cancel")
            }
        }
    )
}
