package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderWithItems
import com.example.ui.theme.BrawnEmeraldAccent
import com.example.ui.theme.BrawnNavyPrimary
import com.example.ui.util.AppLanguage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DaySales(
    val dayLabel: String,
    val amount: Double
)

@Composable
fun SalesChart(
    orders: List<OrderWithItems>,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val isArabic = language == AppLanguage.ARABIC

    // Calculate sales for past 6 days + today
    val salesData = remember(orders, language) {
        val list = mutableListOf<DaySales>()
        val cal = Calendar.getInstance()
        val sdfAr = SimpleDateFormat("EEE", Locale("ar", "SA"))
        val sdfEn = SimpleDateFormat("EEE", Locale.US)

        for (i in 6 downTo 0) {
            val targetCal = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -i)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val startDay = targetCal.timeInMillis
            val endDay = startDay + (24 * 60 * 60 * 1000L)

            val dayTotal = orders.filter {
                it.order.orderDate in startDay until endDay
            }.sumOf { it.order.totalAmount }

            val label = if (isArabic) sdfAr.format(Date(startDay)) else sdfEn.format(Date(startDay))
            list.add(DaySales(label, dayTotal))
        }
        list
    }

    val maxAmount = (salesData.maxOfOrNull { it.amount } ?: 1000.0).coerceAtLeast(100.0)

    val progress = remember { Animatable(0f) }
    LaunchedEffect(orders) {
        progress.snapTo(0f)
        progress.animateTo(1f, animationSpec = tween(durationMillis = 600))
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
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
                Column {
                    Text(
                        text = if (isArabic) "مؤشر مبيعات الأسبوع" else "Weekly Sales Trend",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isArabic) "مبيعات آخر 7 أيام" else "Last 7 days performance",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .background(Color(0xFFE0F2F1), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isArabic) "محدث الآن" else "Live",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrawnEmeraldAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Bar Chart Canvas
            val primaryColor = BrawnNavyPrimary
            val accentColor = BrawnEmeraldAccent
            val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val width = size.width
                    val height = size.height
                    val barCount = salesData.size
                    val spacing = width / barCount
                    val barWidth = spacing * 0.42f

                    // Draw 3 horizontal baseline reference guides
                    for (i in 0..2) {
                        val y = height * (i / 2.5f)
                        drawLine(
                            color = gridColor,
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1f
                        )
                    }

                    // Draw bars
                    salesData.forEachIndexed { index, day ->
                        val barHeight = ((day.amount / maxAmount) * height * 0.85f * progress.value).toFloat()
                            .coerceAtLeast(8f)
                        val left = (index * spacing) + (spacing - barWidth) / 2
                        val top = height - barHeight

                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(accentColor, primaryColor),
                                startY = top,
                                endY = height
                            ),
                            topLeft = Offset(left, top),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(8f, 8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Day labels below bars
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                salesData.forEach { day ->
                    Text(
                        text = day.dayLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
