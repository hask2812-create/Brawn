package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.OrderDao
import com.example.data.dao.PharmacyDao
import com.example.data.dao.ProductDao
import com.example.data.dao.UserDao
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemEntity
import com.example.data.model.PharmacyEntity
import com.example.data.model.ProductEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        ProductEntity::class,
        PharmacyEntity::class,
        OrderEntity::class,
        OrderItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun pharmacyDao(): PharmacyDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "brawn_sales_database"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }

        private suspend fun populateInitialData(database: AppDatabase) {
            val userDao = database.userDao()
            val productDao = database.productDao()
            val pharmacyDao = database.pharmacyDao()
            val orderDao = database.orderDao()

            // 1. Initial Users
            val adminUser = UserEntity(
                id = 1,
                name = "د. أحمد البراوي (مدير المبيعات)",
                email = "admin@brawnpharma.com",
                phone = "0500112233",
                role = UserRole.ADMIN,
                territory = "الإدارة العامة"
            )
            val rep1 = UserEntity(
                id = 2,
                name = "د. عمر عبد الله (مندوب الرياض)",
                email = "omar@brawnpharma.com",
                phone = "0551234567",
                role = UserRole.SALES_REP,
                territory = "المنطقة الوسطى"
            )
            val rep2 = UserEntity(
                id = 3,
                name = "د. سارة القحطاني (مندوبة جدة)",
                email = "sara@brawnpharma.com",
                phone = "0549876543",
                role = UserRole.SALES_REP,
                territory = "المنطقة الغربية"
            )
            userDao.insertAllUsers(listOf(adminUser, rep1, rep2))

            // 2. Initial BRAWN Pharmaceutical Products
            val products = listOf(
                ProductEntity(
                    id = 1,
                    name = "BRAWN Amoxi-Clav",
                    concentration = "1000 mg",
                    dosageForm = "أقراص / Tablets",
                    price = 65.0,
                    stockQuantity = 450,
                    totalSold = 180,
                    activeIngredient = "Amoxicillin + Clavulanic Acid"
                ),
                ProductEntity(
                    id = 2,
                    name = "BRAWN Paracetamol Extra",
                    concentration = "500 mg + 65 mg",
                    dosageForm = "أقراص / Tablets",
                    price = 18.5,
                    stockQuantity = 800,
                    totalSold = 420,
                    activeIngredient = "Paracetamol + Caffeine"
                ),
                ProductEntity(
                    id = 3,
                    name = "BRAWN Omeprazole",
                    concentration = "20 mg",
                    dosageForm = "كبسولات / Capsules",
                    price = 32.0,
                    stockQuantity = 380,
                    totalSold = 210,
                    activeIngredient = "Omeprazole"
                ),
                ProductEntity(
                    id = 4,
                    name = "BRAWN Cefixime",
                    concentration = "400 mg",
                    dosageForm = "كبسولات / Capsules",
                    price = 54.0,
                    stockQuantity = 290,
                    totalSold = 140,
                    activeIngredient = "Cefixime Trihydrate"
                ),
                ProductEntity(
                    id = 5,
                    name = "BRAWN Azithromycin",
                    concentration = "500 mg",
                    dosageForm = "أقراص / Tablets",
                    price = 48.0,
                    stockQuantity = 320,
                    totalSold = 160,
                    activeIngredient = "Azithromycin"
                ),
                ProductEntity(
                    id = 6,
                    name = "BRAWN Ibuprofen Forte",
                    concentration = "400 mg",
                    dosageForm = "كبسولات جيلاتينية / Softgels",
                    price = 24.0,
                    stockQuantity = 520,
                    totalSold = 290,
                    activeIngredient = "Ibuprofen"
                ),
                ProductEntity(
                    id = 7,
                    name = "BRAWN Cetirizine Allergy",
                    concentration = "10 mg",
                    dosageForm = "أقراص / Tablets",
                    price = 22.0,
                    stockQuantity = 460,
                    totalSold = 195,
                    activeIngredient = "Cetirizine HCl"
                ),
                ProductEntity(
                    id = 8,
                    name = "BRAWN Pediatric Syrup",
                    concentration = "120 ml",
                    dosageForm = "شراب / Syrup",
                    price = 27.5,
                    stockQuantity = 310,
                    totalSold = 115,
                    activeIngredient = "Ivy Leaf Extract"
                ),
                ProductEntity(
                    id = 9,
                    name = "BRAWN Vitamin D3 Forte",
                    concentration = "50,000 IU",
                    dosageForm = "كبسولات / Capsules",
                    price = 78.0,
                    stockQuantity = 260,
                    totalSold = 150,
                    activeIngredient = "Cholecalciferol"
                ),
                ProductEntity(
                    id = 10,
                    name = "BRAWN Diclofenac Gel",
                    concentration = "1% (50g)",
                    dosageForm = "مرهم / Gel",
                    price = 19.0,
                    stockQuantity = 400,
                    totalSold = 85,
                    activeIngredient = "Diclofenac Diethylamine"
                )
            )
            productDao.insertAllProducts(products)

            // 3. Initial Pharmacies
            val now = System.currentTimeMillis()
            val dayMillis = 24 * 60 * 60 * 1000L

            val pharmacies = listOf(
                PharmacyEntity(
                    id = 1,
                    name = "صيدلية النور الحديثة",
                    pharmacistName = "د. محمد المنصور",
                    phone = "0501234567",
                    address = "الرياض - حي العليا، طريق الملك فهد",
                    totalPurchases = 4250.0,
                    lastOrderDate = now - (dayMillis * 1),
                    assignedRepId = 2
                ),
                PharmacyEntity(
                    id = 2,
                    name = "صيدلية الشفاء المركزية",
                    pharmacistName = "د. خالد السعيد",
                    phone = "0542345678",
                    address = "جدة - حي الروضة، شارع صاري",
                    totalPurchases = 5890.0,
                    lastOrderDate = now - (dayMillis * 2),
                    assignedRepId = 3
                ),
                PharmacyEntity(
                    id = 3,
                    name = "صيدلية الأمل الطبية",
                    pharmacistName = "د. فاطمة الزهراني",
                    phone = "0563456789",
                    address = "الدمام - حي الشاطئ",
                    totalPurchases = 3120.0,
                    lastOrderDate = now - (dayMillis * 3),
                    assignedRepId = 2
                ),
                PharmacyEntity(
                    id = 4,
                    name = "صيدلية النهضة الكبرى",
                    pharmacistName = "د. طارق الغامدي",
                    phone = "0554567890",
                    address = "الرياض - حي الملقا، طريق أنس بن مالك",
                    totalPurchases = 6450.0,
                    lastOrderDate = now - (dayMillis * 0), // Today
                    assignedRepId = 2
                ),
                PharmacyEntity(
                    id = 5,
                    name = "صيدلية الحياة والعافية",
                    pharmacistName = "د. ريم العتيبي",
                    phone = "0535678901",
                    address = "مكة المكرمة - حي العزيزية",
                    totalPurchases = 2780.0,
                    lastOrderDate = now - (dayMillis * 4),
                    assignedRepId = 3
                )
            )
            pharmacyDao.insertAllPharmacies(pharmacies)

            // 4. Initial Sample Orders
            val order1 = OrderEntity(
                id = 1,
                orderNumber = "BRW-2026-101",
                pharmacyId = 4,
                pharmacyName = "صيدلية النهضة الكبرى",
                repId = 2,
                repName = "د. عمر عبد الله",
                orderDate = now - (2 * 60 * 60 * 1000L), // 2 hours ago today
                totalAmount = 2170.0,
                totalQuantity = 45,
                paymentStatus = "نقداً / Cash",
                notes = "طلبية عاجلة - تسليم صباح الغد"
            )
            val order1Items = listOf(
                OrderItemEntity(
                    orderId = 1,
                    productId = 1,
                    productName = "BRAWN Amoxi-Clav",
                    concentration = "1000 mg",
                    dosageForm = "أقراص / Tablets",
                    unitPrice = 65.0,
                    quantity = 20,
                    subtotal = 1300.0
                ),
                OrderItemEntity(
                    orderId = 1,
                    productId = 2,
                    productName = "BRAWN Paracetamol Extra",
                    concentration = "500 mg + 65 mg",
                    dosageForm = "أقراص / Tablets",
                    unitPrice = 18.5,
                    quantity = 20,
                    subtotal = 370.0
                ),
                OrderItemEntity(
                    orderId = 1,
                    productId = 5,
                    productName = "BRAWN Azithromycin",
                    concentration = "500 mg",
                    dosageForm = "أقراص / Tablets",
                    unitPrice = 48.0,
                    quantity = 10,
                    subtotal = 480.0
                )
            )

            val order2 = OrderEntity(
                id = 2,
                orderNumber = "BRW-2026-100",
                pharmacyId = 1,
                pharmacyName = "صيدلية النور الحديثة",
                repId = 2,
                repName = "د. عمر عبد الله",
                orderDate = now - dayMillis,
                totalAmount = 1420.0,
                totalQuantity = 35,
                paymentStatus = "آجل / Credit",
                notes = "سداد خلال 30 يوم"
            )
            val order2Items = listOf(
                OrderItemEntity(
                    orderId = 2,
                    productId = 3,
                    productName = "BRAWN Omeprazole",
                    concentration = "20 mg",
                    dosageForm = "كبسولات / Capsules",
                    unitPrice = 32.0,
                    quantity = 25,
                    subtotal = 800.0
                ),
                OrderItemEntity(
                    orderId = 2,
                    productId = 9,
                    productName = "BRAWN Vitamin D3 Forte",
                    concentration = "50,000 IU",
                    dosageForm = "كبسولات / Capsules",
                    unitPrice = 78.0,
                    quantity = 5,
                    subtotal = 390.0
                ),
                OrderItemEntity(
                    orderId = 2,
                    productId = 7,
                    productName = "BRAWN Cetirizine Allergy",
                    concentration = "10 mg",
                    dosageForm = "أقراص / Tablets",
                    unitPrice = 22.0,
                    quantity = 10,
                    subtotal = 220.0
                )
            )

            orderDao.insertOrder(order1)
            orderDao.insertOrderItems(order1Items)
            orderDao.insertOrder(order2)
            orderDao.insertOrderItems(order2Items)
        }
    }
}
