package com.example.ui.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.data.model.OrderWithItems
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppLanguage {
    ARABIC,
    ENGLISH
}

object LanguageManager {
    fun formatCurrency(amount: Double, language: AppLanguage): String {
        val formatter = NumberFormat.getNumberInstance(
            if (language == AppLanguage.ARABIC) Locale("ar", "SA") else Locale.US
        )
        formatter.minimumFractionDigits = 2
        formatter.maximumFractionDigits = 2
        val formatted = formatter.format(amount)
        return if (language == AppLanguage.ARABIC) "$formatted ر.س" else "$formatted SAR"
    }

    fun formatDate(timestamp: Long, language: AppLanguage): String {
        val sdf = SimpleDateFormat(
            "dd MMM yyyy - hh:mm a",
            if (language == AppLanguage.ARABIC) Locale("ar", "SA") else Locale.US
        )
        return sdf.format(Date(timestamp))
    }

    fun formatDateOnly(timestamp: Long, language: AppLanguage): String {
        val sdf = SimpleDateFormat(
            "dd/MM/yyyy",
            if (language == AppLanguage.ARABIC) Locale("ar", "SA") else Locale.US
        )
        return sdf.format(Date(timestamp))
    }

    fun exportOrdersToCsv(context: Context, orders: List<OrderWithItems>, language: AppLanguage) {
        val sb = java.lang.StringBuilder()
        // UTF-8 BOM for Microsoft Excel Arabic support
        sb.append("\uFEFF")

        if (language == AppLanguage.ARABIC) {
            sb.append("رقم الطلب,التاريخ,الصيدلية,المندوب,حالة الدفع,إجمالي الكمية,المبلغ الإجمالي (ر.س),المنتجات,ملاحظات\n")
        } else {
            sb.append("Order No,Date,Pharmacy,Representative,Payment Status,Total Qty,Total Amount (SAR),Products,Notes\n")
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)

        for (item in orders) {
            val order = item.order
            val productsSummary = item.items.joinToString(" | ") {
                "${it.productName} (${it.quantity}x)"
            }.replace(",", " - ")

            val dateStr = sdf.format(Date(order.orderDate))
            sb.append("\"${order.orderNumber}\",")
            sb.append("\"$dateStr\",")
            sb.append("\"${order.pharmacyName.replace("\"", "\"\"")}\",")
            sb.append("\"${order.repName.replace("\"", "\"\"")}\",")
            sb.append("\"${order.paymentStatus}\",")
            sb.append("${order.totalQuantity},")
            sb.append("${order.totalAmount},")
            sb.append("\"$productsSummary\",")
            sb.append("\"${order.notes.replace("\"", "\"\"")}\"\n")
        }

        try {
            val fileName = "BRAWN_SALES_REPORT_${System.currentTimeMillis()}.csv"
            val file = File(context.cacheDir, fileName)
            file.writeText(sb.toString(), Charsets.UTF_8)

            val fileUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_SUBJECT, "BRAWN Sales Report - تقرير مبيعات براون")
                putExtra(Intent.EXTRA_STREAM, fileUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(shareIntent, "مشاركة تقرير المبيعات / Export Report").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            // Fallback to plain text share if FileProvider fails
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "BRAWN Sales Report")
                putExtra(Intent.EXTRA_TEXT, sb.toString())
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(shareIntent, "BRAWN Report").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }
}
