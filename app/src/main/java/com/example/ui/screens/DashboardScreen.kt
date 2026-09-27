package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderWithItems
import com.example.data.model.ProductEntity
import com.example.ui.components.SalesChart
import com.example.ui.components.StatCard
import com.example.ui.theme.BrawnBlueLight
import com.example.ui.theme.BrawnEmeraldAccent
import com.example.ui.theme.BrawnEmeraldGreen
import com.example.ui.theme.BrawnNavyPrimary
import com.example.ui.util.AppLanguage
import com.example.ui.util.LanguageManager
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BrawnViewModel
import java.util.Calendar

@Composable
fun DashboardScreen(
    viewModel: BrawnViewModel,
    orders: List<OrderWithItems>,
    products: List<ProductEntity>,
    pharmaciesCount: Int,
    language: AppLanguage,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val isArabic = language == AppLanguage.ARABIC

    // Calculations
    val startOfToday = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    val todayOrders = remember(orders) {
        orders.filter { it.order.orderDate >= startOfToday }
    }
    val todaySales = remember(todayOrders) {
        todayOrders.sumOf { it.order.totalAmount }
    }
    val totalSales = remember(orders) {
        orders.sumOf { it.order.totalAmount }
    }
    val totalQuantitySold = remember(orders) {
        orders.sumOf { it.order.totalQuantity }
    }
    val averageOrderValue = remember(orders) {
        if (orders.isNotEmpty()) totalSales / orders.size else 0.0
    }

    // Top selling products sorted by totalSold
    val topSelling = remember(products) {
        products.sortedByDescending { it.totalSold }.take(4)
    }
    val maxSold = remember(topSelling) {
        topSelling.maxOfOrNull { it.totalSold } ?: 1
    }

    // Recent orders (last 4)
    val recentOrders = remember(orders) {
        orders.take(4)
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Welcome & Quick Action Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = BrawnNavyPrimary),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = if (isArabic) "مرحباً بك في نظام مبيعات براون" else "Welcome to BRAWN Sales",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isArabic) "تسجيل ومتابعة المبيعات الصيدلانية بسرعة وسهولة" else "Fast & streamlined pharmaceutical sales tracking",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { onNavigate(AppScreen.NEW_ORDER) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrawnEmeraldAccent,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("dashboard_new_order_button")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isArabic) "تسجيل طلبية مبيعات جديدة" else "Create New Sales Order",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Stat Cards Row 1: Today's Sales & Total Sales
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = if (isArabic) "مبيعات اليوم" else "Today's Sales",
                        value = LanguageManager.formatCurrency(todaySales, language),
                        subtitle = if (isArabic) "${todayOrders.size} طلبات اليوم" else "${todayOrders.size} orders today",
                        icon = Icons.Default.PointOfSale,
                        iconBackground = Color(0xFFE0F2FE),
                        iconTint = BrawnBlueLight,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = if (isArabic) "إجمالي المبيعات" else "Total Revenue",
                        value = LanguageManager.formatCurrency(totalSales, language),
                        subtitle = if (isArabic) "${orders.size} إجمالي الطلبات" else "${orders.size} total orders",
                        icon = Icons.Default.AttachMoney,
                        iconBackground = Color(0xFFE8F5E9),
                        iconTint = BrawnEmeraldGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Stat Cards Row 2: Quantities, Pharmacies & Avg Order
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = if (isArabic) "الكميات المباعة" else "Quantity Sold",
                        value = "$totalQuantitySold " + if (isArabic) "عبوة" else "units",
                        subtitle = if (isArabic) "جميع المنتجات" else "All products",
                        icon = Icons.Default.Inventory2,
                        iconBackground = Color(0xFFFEF3C7),
                        iconTint = Color(0xFFD97706),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = if (isArabic) "عدد الصيدليات" else "Pharmacies",
                        value = "$pharmaciesCount " + if (isArabic) "صيدلية" else "clients",
                        subtitle = if (isArabic) "شبكة عملاء براون" else "Brawn network",
                        icon = Icons.Default.LocalPharmacy,
                        iconBackground = Color(0xFFEDE9FE),
                        iconTint = Color(0xFF7C3AED),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Sales Chart
            item {
                SalesChart(
                    orders = orders,
                    language = language
                )
            }

            // Top Selling Products Section
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
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isArabic) "أكثر منتجات BRAWN مبيعاً" else "Top Selling Products",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onNavigate(AppScreen.PRODUCTS) }
                            ) {
                                Text(
                                    text = if (isArabic) "عرض الكل" else "View All",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        topSelling.forEach { product ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = product.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${product.concentration} • ${product.dosageForm}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "${product.totalSold} " + if (isArabic) "مباع" else "sold",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = BrawnEmeraldAccent
                                        )
                                        Text(
                                            text = LanguageManager.formatCurrency(product.price, language),
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                val ratio = (product.totalSold.toFloat() / maxSold).coerceIn(0f, 1f)
                                LinearProgressIndicator(
                                    progress = { ratio },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = BrawnEmeraldAccent,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Recent Orders Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isArabic) "آخر الطلبيات المسجلة" else "Recent Orders",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isArabic) "عرض الكل (${orders.size})" else "View All (${orders.size})",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable { onNavigate(AppScreen.ORDERS) }
                            .padding(4.dp)
                    )
                }
            }

            items(recentOrders) { item ->
                val order = item.order
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.openOrderDetails(item) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFE0F2FE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Receipt,
                                contentDescription = null,
                                tint = BrawnNavyPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = order.pharmacyName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${order.orderNumber} • ${LanguageManager.formatDateOnly(order.orderDate, language)}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${order.totalQuantity} " + if (isArabic) "عبوات" else "items" + " • " + order.paymentStatus,
                                fontSize = 11.sp,
                                color = if (order.paymentStatus.contains("نقداً") || order.paymentStatus.contains("Cash")) BrawnEmeraldAccent else Color(0xFFD97706)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = LanguageManager.formatCurrency(order.totalAmount, language),
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { onNavigate(AppScreen.NEW_ORDER) },
            containerColor = BrawnEmeraldAccent,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_new_order")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Order")
        }
    }
}
