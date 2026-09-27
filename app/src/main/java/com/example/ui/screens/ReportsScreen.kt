package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.OrderWithItems
import com.example.data.model.PharmacyEntity
import com.example.data.model.ProductEntity
import com.example.ui.components.StatCard
import com.example.ui.theme.BrawnBlueLight
import com.example.ui.theme.BrawnEmeraldAccent
import com.example.ui.theme.BrawnEmeraldGreen
import com.example.ui.theme.BrawnNavyPrimary
import com.example.ui.util.AppLanguage
import com.example.ui.util.LanguageManager
import com.example.ui.viewmodel.BrawnViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: BrawnViewModel,
    orders: List<OrderWithItems>,
    products: List<ProductEntity>,
    pharmacies: List<PharmacyEntity>,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val isArabic = language == AppLanguage.ARABIC
    val context = LocalContext.current

    // Period selector: 0 = Daily (اليوم), 1 = Weekly (الأسبوع), 2 = Monthly (الشهر), 3 = All Time (الكل)
    var selectedPeriodTab by remember { mutableIntStateOf(1) }
    val tabTitles = if (isArabic) {
        listOf("اليوم", "هذا الأسبوع", "هذا الشهر", "الكل")
    } else {
        listOf("Today", "This Week", "This Month", "All Time")
    }

    // Filter orders by selected period
    val filteredPeriodOrders = remember(orders, selectedPeriodTab) {
        val now = Calendar.getInstance()
        when (selectedPeriodTab) {
            0 -> { // Daily
                val startOfToday = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                orders.filter { it.order.orderDate >= startOfToday }
            }
            1 -> { // Weekly
                val startOfWeek = Calendar.getInstance().apply {
                    set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                orders.filter { it.order.orderDate >= startOfWeek }
            }
            2 -> { // Monthly
                val startOfMonth = Calendar.getInstance().apply {
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                orders.filter { it.order.orderDate >= startOfMonth }
            }
            else -> orders
        }
    }

    // KPI Metrics for the selected period
    val periodTotalSales = remember(filteredPeriodOrders) {
        filteredPeriodOrders.sumOf { it.order.totalAmount }
    }
    val periodTotalQuantity = remember(filteredPeriodOrders) {
        filteredPeriodOrders.sumOf { it.order.totalQuantity }
    }
    val periodOrderCount = filteredPeriodOrders.size
    val periodAvgOrderValue = if (periodOrderCount > 0) periodTotalSales / periodOrderCount else 0.0

    // Top selling products ranking
    val topSellingList = remember(products) {
        products.sortedByDescending { it.totalSold }.take(5)
    }

    // Top pharmacies ranking
    val topPharmaciesList = remember(pharmacies) {
        pharmacies.sortedByDescending { it.totalPurchases }.take(5)
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title & Export Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isArabic) "تقارير المبيعات والتحليلات" else "Sales Analytics & Reports",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isArabic) "إحصائيات دورية دقيقة ومفصلة" else "Detailed performance breakdown",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { viewModel.exportCsvReport(context) },
                    colors = ButtonDefaults.buttonColors(containerColor = BrawnEmeraldAccent),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("export_csv_button")
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isArabic) "تصدير CSV" else "Export CSV",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Period Filter Tabs
        item {
            PrimaryTabRow(
                selectedTabIndex = selectedPeriodTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = BrawnNavyPrimary
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedPeriodTab == index,
                        onClick = { selectedPeriodTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedPeriodTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }
        }

        // Period Summary Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = if (isArabic) "ملخص مبيعات: ${tabTitles[selectedPeriodTab]}" else "Summary: ${tabTitles[selectedPeriodTab]}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            title = if (isArabic) "إجمالي المبيعات" else "Total Revenue",
                            value = LanguageManager.formatCurrency(periodTotalSales, language),
                            subtitle = if (isArabic) "$periodOrderCount طلب" else "$periodOrderCount orders",
                            icon = Icons.Default.AttachMoney,
                            iconBackground = Color(0xFFE8F5E9),
                            iconTint = BrawnEmeraldGreen,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = if (isArabic) "إجمالي الكميات" else "Quantity Sold",
                            value = "$periodTotalQuantity " + if (isArabic) "عبوة" else "units",
                            subtitle = if (isArabic) "متوسط ${periodTotalQuantity / if (periodOrderCount > 0) periodOrderCount else 1} بالطلب" else "avg per order",
                            icon = Icons.Default.Inventory2,
                            iconBackground = Color(0xFFFEF3C7),
                            iconTint = Color(0xFFD97706),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            title = if (isArabic) "متوسط قيمة الطلب" else "Avg Order Value",
                            value = LanguageManager.formatCurrency(periodAvgOrderValue, language),
                            subtitle = if (isArabic) "معدل الفاتورة" else "Per ticket average",
                            icon = Icons.Default.PointOfSale,
                            iconBackground = Color(0xFFE0F2FE),
                            iconTint = BrawnBlueLight,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = if (isArabic) "عدد الطلبيات" else "Orders Placed",
                            value = "$periodOrderCount " + if (isArabic) "طلب" else "orders",
                            subtitle = if (isArabic) "خلال الفترة" else "In selected period",
                            icon = Icons.Default.Assessment,
                            iconBackground = Color(0xFFEDE9FE),
                            iconTint = Color(0xFF7C3AED),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Section: Top Selling BRAWN Products
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
                            Icon(
                                imageVector = Icons.Default.Leaderboard,
                                contentDescription = null,
                                tint = BrawnNavyPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isArabic) "أكثر المنتجات مبيعًا (Top Products)" else "Top Selling Products",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    topSellingList.forEachIndexed { index, product ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (index) {
                                                0 -> Color(0xFFFEF08A)
                                                1 -> Color(0xFFE2E8F0)
                                                2 -> Color(0xFFFED7AA)
                                                else -> MaterialTheme.colorScheme.surfaceVariant
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp,
                                        color = BrawnNavyPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
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
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${product.totalSold} " + if (isArabic) "عبوة مباعة" else "units sold",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = BrawnEmeraldAccent
                                )
                                Text(
                                    text = LanguageManager.formatCurrency(product.price * product.totalSold, language),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        if (index < topSellingList.lastIndex) {
                            Divider(modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            }
        }

        // Section: Top Purchasing Pharmacies
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
                                .background(Color(0xFFE8F5E9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalPharmacy,
                                contentDescription = null,
                                tint = BrawnEmeraldGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isArabic) "أكثر الصيدليات شراءً (Top Clients)" else "Top Purchasing Pharmacies",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    topPharmaciesList.forEachIndexed { index, pharmacy ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE2E8F0)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = BrawnNavyPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = pharmacy.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${pharmacy.pharmacistName} • ${pharmacy.address}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = LanguageManager.formatCurrency(pharmacy.totalPurchases, language),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = BrawnNavyPrimary
                                )
                                Text(
                                    text = if (isArabic) "إجمالي المشتريات" else "Total Purchases",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        if (index < topPharmaciesList.lastIndex) {
                            Divider(modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            }
        }

        // Export Callout
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isArabic) "تصدير التقرير الكامل كملف Excel / CSV" else "Export Full Data to CSV / Excel",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = BrawnEmeraldAccent
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isArabic) "يمكنك فتح الملف في Excel أو مشاركته عبر البريد والواتساب" else "Compatible with Excel, WhatsApp, and email",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = { viewModel.exportCsvReport(context) },
                        colors = ButtonDefaults.buttonColors(containerColor = BrawnEmeraldAccent),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (isArabic) "مشاركة" else "Share", fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}
