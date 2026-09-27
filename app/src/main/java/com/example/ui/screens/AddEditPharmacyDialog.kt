package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.data.model.PharmacyEntity
import com.example.ui.theme.BrawnNavyPrimary
import com.example.ui.util.AppLanguage

@Composable
fun AddEditPharmacyDialog(
    pharmacy: PharmacyEntity?,
    language: AppLanguage,
    onSave: (name: String, pharmacistName: String, phone: String, address: String, notes: String) -> Unit,
    onDismiss: () -> Unit
) {
    val isArabic = language == AppLanguage.ARABIC
    val isEditing = pharmacy != null

    var name by remember { mutableStateOf(pharmacy?.name ?: "") }
    var pharmacistName by remember { mutableStateOf(pharmacy?.pharmacistName ?: "") }
    var phone by remember { mutableStateOf(pharmacy?.phone ?: "") }
    var address by remember { mutableStateOf(pharmacy?.address ?: "") }
    var notes by remember { mutableStateOf(pharmacy?.notes ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEditing) {
                    if (isArabic) "تعديل بيانات الصيدلية" else "Edit Pharmacy"
                } else {
                    if (isArabic) "إضافة صيدلية جديدة للعملاء" else "Add New Pharmacy"
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
                    label = { Text(if (isArabic) "اسم الصيدلية *" else "Pharmacy Name *") },
                    placeholder = { Text("e.g. صيدلية النور الحديثة") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pharmacy_name_input")
                )

                OutlinedTextField(
                    value = pharmacistName,
                    onValueChange = { pharmacistName = it; errorMessage = null },
                    label = { Text(if (isArabic) "اسم الصيدلي المسئول *" else "Responsible Pharmacist *") },
                    placeholder = { Text("e.g. د. محمد المنصور") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pharmacist_name_input")
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it; errorMessage = null },
                    label = { Text(if (isArabic) "رقم الهاتف *" else "Phone Number *") },
                    placeholder = { Text("05xxxxxxxx") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pharmacy_phone_input")
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it; errorMessage = null },
                    label = { Text(if (isArabic) "العنوان والمدينة *" else "Address & City *") },
                    placeholder = { Text("e.g. الرياض - حي العليا، طريق الملك فهد") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pharmacy_address_input")
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(if (isArabic) "ملاحظات إضافية" else "Additional Notes") },
                    placeholder = { Text("e.g. أوقات الزيارة المناسبة...") },
                    maxLines = 2,
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
                        errorMessage = if (isArabic) "يرجى إدخال اسم الصيدلية" else "Please enter pharmacy name"
                        return@Button
                    }
                    if (pharmacistName.isBlank()) {
                        errorMessage = if (isArabic) "يرجى إدخال اسم الصيدلي" else "Please enter pharmacist name"
                        return@Button
                    }
                    if (phone.isBlank()) {
                        errorMessage = if (isArabic) "يرجى إدخال رقم الهاتف" else "Please enter phone number"
                        return@Button
                    }
                    if (address.isBlank()) {
                        errorMessage = if (isArabic) "يرجى إدخال العنوان" else "Please enter address"
                        return@Button
                    }

                    onSave(name.trim(), pharmacistName.trim(), phone.trim(), address.trim(), notes.trim())
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrawnNavyPrimary),
                modifier = Modifier.testTag("save_pharmacy_confirm_button")
            ) {
                Text(if (isArabic) "حفظ الصيدلية" else "Save Pharmacy")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isArabic) "إلغاء" else "Cancel")
            }
        }
    )
}
