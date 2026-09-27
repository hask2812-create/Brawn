package com.example.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrawnEmeraldAccent
import com.example.ui.theme.BrawnNavyPrimary
import com.example.ui.util.AppLanguage
import com.example.ui.viewmodel.AppScreen

@Composable
fun BrawnBottomNavigation(
    currentScreen: AppScreen,
    currentLanguage: AppLanguage,
    onSelectScreen: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val isArabic = currentLanguage == AppLanguage.ARABIC

    NavigationBar(
        modifier = modifier.navigationBarsPadding(),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        // 1. Dashboard
        NavItem(
            selected = currentScreen == AppScreen.DASHBOARD,
            onClick = { onSelectScreen(AppScreen.DASHBOARD) },
            icon = Icons.Default.Dashboard,
            label = if (isArabic) "الرئيسية" else "Home",
            tag = "nav_dashboard"
        )

        // 2. Orders
        NavItem(
            selected = currentScreen == AppScreen.ORDERS,
            onClick = { onSelectScreen(AppScreen.ORDERS) },
            icon = Icons.Default.ReceiptLong,
            label = if (isArabic) "الطلبات" else "Orders",
            tag = "nav_orders"
        )

        // 3. New Order (Special highlight)
        NavItem(
            selected = currentScreen == AppScreen.NEW_ORDER,
            onClick = { onSelectScreen(AppScreen.NEW_ORDER) },
            icon = Icons.Default.AddShoppingCart,
            label = if (isArabic) "طلب جديد" else "New Order",
            tag = "nav_new_order",
            isAccent = true
        )

        // 4. Products
        NavItem(
            selected = currentScreen == AppScreen.PRODUCTS,
            onClick = { onSelectScreen(AppScreen.PRODUCTS) },
            icon = Icons.Default.Medication,
            label = if (isArabic) "الأدوية" else "Products",
            tag = "nav_products"
        )

        // 5. Pharmacies
        NavItem(
            selected = currentScreen == AppScreen.PHARMACIES,
            onClick = { onSelectScreen(AppScreen.PHARMACIES) },
            icon = Icons.Default.LocalPharmacy,
            label = if (isArabic) "الصيدليات" else "Pharmacies",
            tag = "nav_pharmacies"
        )

        // 6. Reports
        NavItem(
            selected = currentScreen == AppScreen.REPORTS,
            onClick = { onSelectScreen(AppScreen.REPORTS) },
            icon = Icons.Default.Assessment,
            label = if (isArabic) "التقارير" else "Reports",
            tag = "nav_reports"
        )
    }
}

@Composable
private fun RowScope.NavItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tag: String,
    isAccent: Boolean = false
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(if (isAccent) 24.dp else 22.dp)
            )
        },
        label = {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1
            )
        },
        alwaysShowLabel = true,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = if (isAccent) BrawnEmeraldAccent else BrawnNavyPrimary,
            selectedTextColor = if (isAccent) BrawnEmeraldAccent else BrawnNavyPrimary,
            indicatorColor = if (isAccent) Color(0xFFD1FAE5) else Color(0xFFE2E8F0),
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        ),
        modifier = Modifier.testTag(tag)
    )
}
