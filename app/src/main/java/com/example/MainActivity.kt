package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.BrawnAppHeader
import com.example.ui.components.BrawnBottomNavigation
import com.example.ui.screens.AddEditPharmacyDialog
import com.example.ui.screens.AddEditProductDialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.NewOrderScreen
import com.example.ui.screens.OrderDetailsDialog
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.PharmaciesScreen
import com.example.ui.screens.ProductsScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.UserSwitchDialog
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.util.AppLanguage
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BrawnViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                BrawnSalesApp()
            }
        }
    }
}

@Composable
fun BrawnSalesApp(
    viewModel: BrawnViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val products by viewModel.filteredProducts.collectAsState()
    val allProductsRaw by viewModel.allProducts.collectAsState()
    val pharmacies by viewModel.filteredPharmacies.collectAsState()
    val allPharmaciesRaw by viewModel.allPharmacies.collectAsState()
    val orders by viewModel.filteredOrders.collectAsState()
    val allOrdersRaw by viewModel.allOrders.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()

    // Dialog states
    val isAddProductOpen by viewModel.isAddProductOpen.collectAsState()
    val productInEdit by viewModel.productInEdit.collectAsState()
    val isAddPharmacyOpen by viewModel.isAddPharmacyOpen.collectAsState()
    val pharmacyInEdit by viewModel.pharmacyInEdit.collectAsState()
    val selectedOrderDetails by viewModel.selectedOrderDetails.collectAsState()
    val isUserSwitchOpen by viewModel.isUserSwitchOpen.collectAsState()

    // Back handling: If on secondary screen, go back to Dashboard
    BackHandler(enabled = currentScreen != AppScreen.DASHBOARD) {
        viewModel.navigateTo(AppScreen.DASHBOARD)
    }

    val layoutDirection = if (currentLanguage == AppLanguage.ARABIC) {
        LayoutDirection.Rtl
    } else {
        LayoutDirection.Ltr
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars),
            topBar = {
                BrawnAppHeader(
                    currentUser = currentUser,
                    currentLanguage = currentLanguage,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onOpenUserSwitch = { viewModel.isUserSwitchOpen.value = true }
                )
            },
            bottomBar = {
                BrawnBottomNavigation(
                    currentScreen = currentScreen,
                    currentLanguage = currentLanguage,
                    onSelectScreen = { viewModel.navigateTo(it) }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                when (currentScreen) {
                    AppScreen.DASHBOARD -> DashboardScreen(
                        viewModel = viewModel,
                        orders = allOrdersRaw,
                        products = allProductsRaw,
                        pharmaciesCount = allPharmaciesRaw.size,
                        language = currentLanguage,
                        onNavigate = { viewModel.navigateTo(it) }
                    )

                    AppScreen.ORDERS -> OrdersScreen(
                        viewModel = viewModel,
                        orders = orders,
                        pharmacies = allPharmaciesRaw,
                        language = currentLanguage,
                        onNavigate = { viewModel.navigateTo(it) }
                    )

                    AppScreen.NEW_ORDER -> NewOrderScreen(
                        viewModel = viewModel,
                        products = allProductsRaw,
                        pharmacies = allPharmaciesRaw,
                        language = currentLanguage
                    )

                    AppScreen.PRODUCTS -> ProductsScreen(
                        viewModel = viewModel,
                        products = products,
                        language = currentLanguage,
                        onNavigate = { viewModel.navigateTo(it) }
                    )

                    AppScreen.PHARMACIES -> PharmaciesScreen(
                        viewModel = viewModel,
                        pharmacies = pharmacies,
                        language = currentLanguage,
                        onNavigate = { viewModel.navigateTo(it) }
                    )

                    AppScreen.REPORTS -> ReportsScreen(
                        viewModel = viewModel,
                        orders = allOrdersRaw,
                        products = allProductsRaw,
                        pharmacies = allPharmaciesRaw,
                        language = currentLanguage
                    )
                }
            }
        }

        // Dialogs
        if (selectedOrderDetails != null) {
            OrderDetailsDialog(
                orderWithItems = selectedOrderDetails!!,
                language = currentLanguage,
                onDismiss = { viewModel.closeOrderDetails() }
            )
        }

        if (isAddProductOpen) {
            AddEditProductDialog(
                product = productInEdit,
                language = currentLanguage,
                onSave = { name, conc, dosage, price, stock, activeIng ->
                    viewModel.saveProduct(name, conc, dosage, price, stock, activeIng)
                },
                onDismiss = { viewModel.closeProductDialog() }
            )
        }

        if (isAddPharmacyOpen) {
            AddEditPharmacyDialog(
                pharmacy = pharmacyInEdit,
                language = currentLanguage,
                onSave = { name, pharmacist, phone, address, notes ->
                    viewModel.savePharmacy(name, pharmacist, phone, address, notes)
                },
                onDismiss = { viewModel.closePharmacyDialog() }
            )
        }

        if (isUserSwitchOpen) {
            UserSwitchDialog(
                currentUser = currentUser,
                allUsers = allUsers,
                language = currentLanguage,
                onSelectUser = { viewModel.switchUser(it) },
                onDismiss = { viewModel.isUserSwitchOpen.value = false }
            )
        }
    }
}
