package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Transaction
import com.example.ui.theme.PolishExpenseRed
import com.example.ui.theme.PolishIncomeGreen
import com.example.ui.theme.ProfessionalBorder
import com.example.ui.theme.ProfessionalPrimary
import com.example.ui.theme.ProfessionalPrimaryContainer
import com.example.ui.theme.TextMutedSubtle
import com.example.ui.theme.TextPrimaryDark
import java.util.Calendar
import java.util.Locale

@Composable
fun SpendingBarChart(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "ChartAnim"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val cal = Calendar.getInstance()

    val dayTotals = FloatArray(7) { 0f }
    val now = System.currentTimeMillis()
    val sevenDaysAgo = now - (7 * 86400000L)

    transactions.filter { it.dateTimestamp >= sevenDaysAgo && it.amount < 0 }.forEach { tx ->
        cal.timeInMillis = tx.dateTimestamp
        var dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY
        if (dayOfWeek < 0) dayOfWeek += 7
        if (dayOfWeek in 0..6) {
            dayTotals[dayOfWeek] += Math.abs(tx.amount).toFloat()
        }
    }

    val maxTotal = (dayTotals.maxOrNull() ?: 100f).coerceAtLeast(100f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, ProfessionalBorder, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Weekly Spending Insights",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    Text(
                        text = "Last 7 Days Outflow",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMutedSubtle
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(ProfessionalPrimaryContainer)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Avg $${String.format(Locale.US, "%.0f", dayTotals.average())}/day",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF001D36)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            val trackColor = Color(0xFFE1E2EC)

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val barWidth = 22.dp.toPx()
                val totalBars = 7
                val spacing = (canvasWidth - (barWidth * totalBars)) / (totalBars + 1)

                for (i in 0 until totalBars) {
                    val x = spacing + i * (barWidth + spacing)
                    val barHeight = ((dayTotals[i] / maxTotal) * (canvasHeight - 16.dp.toPx())) * progress

                    // Draw Background Track
                    drawRoundRect(
                        color = trackColor,
                        topLeft = Offset(x, 0f),
                        size = Size(barWidth, canvasHeight),
                        cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
                    )

                    // Draw Active Bar
                    if (barHeight > 0) {
                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(ProfessionalPrimary, Color(0xFF004A77))
                            ),
                            topLeft = Offset(x, canvasHeight - barHeight),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                dayLabels.forEachIndexed { idx, label ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (dayTotals[idx] > 0) TextPrimaryDark else TextMutedSubtle
                    )
                }
            }
        }
    }
}
