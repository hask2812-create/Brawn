package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PharmacyEntity
import com.example.data.model.ProductEntity
import com.example.ui.theme.BrawnEmeraldAccent
import com.example.ui.theme.BrawnNavyPrimary
import com.example.ui.util.AppLanguage
import com.example.ui.util.LanguageManager
import com.example.ui.viewmodel.BrawnViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOrderScreen(
    viewModel: BrawnViewModel,
    products: List<ProductEntity>,
    pharmacies: List<PharmacyEntity>,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val isArabic = language == AppLanguage.ARABIC
    val context = LocalContext.current

    val selectedPharmacy by viewModel.selectedPharmacy.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val totalAmount by viewModel.cartTotalAmount.collectAsState()
    val totalQuantity by viewModel.cartTotalQuantity.collectAsState()
    val paymentStatus by viewModel.orderPaymentStatus.collectAsState()
    val orderNotes by viewModel.orderNotes.collectAsState()

    var showPharmacyPicker by remember { mutableStateOf(false) }
    var pharmacySearchText by remember { mutableStateOf("") }
    var productSearchText by remember { mutableStateOf("") }
    var selectedDosageFilter by remember { mutableStateOf("الكل") }

    val dosageOptions = listOf("الكل", "أقراص", "كبسولات", "شراب", "مرهم")

    val filteredProductList = remember(products, productSearchText, selectedDosageFilter) {
        products.filter { p ->
            val matchesQuery = productSearchText.isBlank() ||
                p.name.contains(productSearchText, ignoreCase = true) ||
                p.concentration.contains(productSearchText, ignoreCase = true)
            val matchesDosage = selectedDosageFilter == "الكل" || p.dosageForm.contains(selectedDosageFilter, ignoreCase = true)
            matchesQuery && matchesDosage
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // STEP 1: Select Pharmacy Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE0F2FE)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "1",
                                        fontWeight = FontWeight.Bold,
                                        color = BrawnNavyPrimary,
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isArabic) "اختيار الصيدلية" else "Select Pharmacy",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            if (selectedPharmacy != null) {
                                TextButton(onClick = { showPharmacyPicker = true }) {
                                    Text(text = if (isArabic) "تغيير" else "Change", fontSize = 13.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (selectedPharmacy == null) {
                            OutlinedButton(
                                onClick = { showPharmacyPicker = true },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("select_pharmacy_button")
                            ) {
                                Icon(imageVector = Icons.Default.LocalPharmacy, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isArabic) "اضغط لاختيار الصيدلية المستهدفة" else "Tap to choose target pharmacy",
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        } else {
                            val pharmacy = selectedPharmacy!!
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF0FDF4),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color(0xFFBBF7D0), RoundedCornerShape(12.dp))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = BrawnEmeraldAccent,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = pharmacy.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${pharmacy.pharmacistName} • ${pharmacy.phone}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = pharmacy.address,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // STEP 2: Products Selection (Multi-Item Order Support)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE0F2FE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "2",
                                    fontWeight = FontWeight.Bold,
                                    color = BrawnNavyPrimary,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isArabic) "إضافة الأدوية والكميات" else "Add Products & Quantities",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Search box for products
                        OutlinedTextField(
                            value = productSearchText,
                            onValueChange = { productSearchText = it },
                            placeholder = { Text(if (isArabic) "بحث عن دواء، تركيز..." else "Search medicine, strength...") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("product_search_input")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Dosage filter chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(dosageOptions) { dosage ->
                                val selected = selectedDosageFilter == dosage
                                FilterChip(
                                    selected = selected,
                                    onClick = { selectedDosageFilter = dosage },
                                    label = { Text(dosage, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = BrawnNavyPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // List of products available to add
                        filteredProductList.forEach { product ->
                            val cartItem = cartItems[product.id]
                            val qtyInCart = cartItem?.quantity ?: 0

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (qtyInCart > 0) Color(0xFFEFF6FF) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = product.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${product.concentration} • ${product.dosageForm}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = LanguageManager.formatCurrency(product.price, language) +
                                                " • " + if (isArabic) "المتوفر: ${product.stockQuantity}" else "Stock: ${product.stockQuantity}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    // Quantity Stepper
                                    if (qtyInCart == 0) {
                                        Button(
                                            onClick = { viewModel.addToCart(product, 1) },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = BrawnNavyPrimary),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                            modifier = Modifier.testTag("add_product_${product.id}")
                                        ) {
                                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(text = if (isArabic) "إضافة" else "Add", fontSize = 12.sp)
                                        }
                                    } else {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            IconButton(
                                                onClick = { viewModel.updateCartItemQuantity(product.id, qtyInCart - 1) },
                                                modifier = Modifier
                                                    .size(30.dp)
                                                    .background(Color.White, CircleShape)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Remove,
                                                    contentDescription = "Minus",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }

                                            Text(
                                                text = "$qtyInCart",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 14.sp,
                                                color = BrawnNavyPrimary,
                                                modifier = Modifier.padding(horizontal = 6.dp)
                                            )

                                            IconButton(
                                                onClick = { viewModel.updateCartItemQuantity(product.id, qtyInCart + 1) },
                                                modifier = Modifier
                                                    .size(30.dp)
                                                    .background(BrawnNavyPrimary, CircleShape)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Plus",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // STEP 3: Order Review & Options (Payment Status & Notes)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE0F2FE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "3",
                                    fontWeight = FontWeight.Bold,
                                    color = BrawnNavyPrimary,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isArabic) "تفاصيل الدفع والملاحظات" else "Payment & Notes",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Selected Items list preview
                        if (cartItems.isNotEmpty()) {
                            Text(
                                text = if (isArabic) "الأصناف المختارة (${cartItems.size}):" else "Selected Items (${cartItems.size}):",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            cartItems.values.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.product.name,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "${item.quantity} × ${LanguageManager.formatCurrency(item.unitPrice, language)}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = LanguageManager.formatCurrency(item.subtotal, language),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = BrawnNavyPrimary
                                    )
                                }
                            }
                            Divider(modifier = Modifier.padding(vertical = 10.dp))
                        }

                        // Payment Status selection
                        Text(
                            text = if (isArabic) "طريقة السداد:" else "Payment Status:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val isCash = paymentStatus.contains("نقداً") || paymentStatus.contains("Cash")
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isCash) Color(0xFFDCFCE7) else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { viewModel.setPaymentStatus("نقداً / Cash") }
                                    .testTag("payment_cash_option")
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isCash) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = BrawnEmeraldAccent, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = if (isArabic) "نقداً / فوري" else "Cash",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isCash) BrawnEmeraldAccent else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            val isCredit = paymentStatus.contains("آجل") || paymentStatus.contains("Credit")
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isCredit) Color(0xFFFEF3C7) else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { viewModel.setPaymentStatus("آجل / Credit") }
                                    .testTag("payment_credit_option")
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isCredit) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = if (isArabic) "آجل / تحصيل" else "Credit",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isCredit) Color(0xFFD97706) else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Notes TextField
                        OutlinedTextField(
                            value = orderNotes,
                            onValueChange = { viewModel.setOrderNotes(it) },
                            label = { Text(if (isArabic) "ملاحظات إضافية (اختياري)" else "Order Notes (optional)") },
                            placeholder = { Text(if (isArabic) "مثال: تسليم بعد الظهر، طلبية دورية..." else "e.g. deliver afternoon...") },
                            maxLines = 2,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("order_notes_input")
                        )
                    }
                }
            }
        }

        // Sticky Bottom Checkout Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 12.dp,
            shadowElevation = 8.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (isArabic) "إجمالي الطلب ($totalQuantity عبوة):" else "Total ($totalQuantity units):",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = LanguageManager.formatCurrency(totalAmount, language),
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = BrawnNavyPrimary
                    )
                }

                Button(
                    onClick = {
                        viewModel.submitOrder(
                            onSuccess = { orderNo ->
                                Toast.makeText(
                                    context,
                                    if (isArabic) "تم حفظ الطلب $orderNo بنجاح!" else "Order $orderNo saved successfully!",
                                    Toast.LENGTH_LONG
                                ).show()
                            },
                            onError = { err ->
                                Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrawnEmeraldAccent,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .height(50.dp)
                        .testTag("submit_order_button")
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "تأكيد وحفظ الطلب" else "Save Order",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }

        // Pharmacy Selector Modal Dialog
        if (showPharmacyPicker) {
            AlertDialog(
                onDismissRequest = { showPharmacyPicker = false },
                title = {
                    Text(
                        text = if (isArabic) "اختر الصيدلية" else "Select Pharmacy",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp)
                    ) {
                        OutlinedTextField(
                            value = pharmacySearchText,
                            onValueChange = { pharmacySearchText = it },
                            placeholder = { Text(if (isArabic) "بحث بالاسم أو العنوان..." else "Search name, address...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        val filteredPharmacies = pharmacies.filter {
                            pharmacySearchText.isBlank() ||
                                it.name.contains(pharmacySearchText, ignoreCase = true) ||
                                it.address.contains(pharmacySearchText, ignoreCase = true)
                        }

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(filteredPharmacies) { p ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (selectedPharmacy?.id == p.id) Color(0xFFEFF6FF) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.selectPharmacy(p)
                                            showPharmacyPicker = false
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocalPharmacy,
                                            contentDescription = null,
                                            tint = BrawnNavyPrimary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = p.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                text = "${p.pharmacistName} • ${p.address}",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        if (selectedPharmacy?.id == p.id) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = BrawnNavyPrimary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showPharmacyPicker = false }) {
                        Text(if (isArabic) "إغلاق" else "Close")
                    }
                }
            )
        }
    }
}
