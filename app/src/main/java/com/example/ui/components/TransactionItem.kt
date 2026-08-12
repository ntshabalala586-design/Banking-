package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Transaction
import com.example.data.TransactionStatus
import com.example.ui.theme.IconBgPeach
import com.example.ui.theme.IconBgSage
import com.example.ui.theme.PolishExpenseRed
import com.example.ui.theme.PolishIncomeGreen
import com.example.ui.theme.PolishPendingYellow
import com.example.ui.theme.ProfessionalBorder
import com.example.ui.theme.TextMutedSubtle
import com.example.ui.theme.TextPrimaryDark
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionItem(
    transaction: Transaction,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val usdFormat = NumberFormat.getCurrencyInstance(Locale.US)
    val isExpense = transaction.amount < 0
    val formattedAmount = usdFormat.format(Math.abs(transaction.amount))

    val (icon, iconBg, iconTint) = when (transaction.category.lowercase(Locale.US)) {
        "food & dining", "food", "dining" -> Triple(Icons.Default.Restaurant, IconBgSage, Color(0xFF00210E))
        "shopping" -> Triple(Icons.Default.ShoppingBag, IconBgPeach, Color(0xFF3B0900))
        "salary", "income" -> Triple(Icons.Default.Work, Color(0xFFE1E2EC), Color(0xFF191C1E))
        "transportation", "travel" -> Triple(Icons.Default.DirectionsCar, Color(0xFFD3E4FF), Color(0xFF001D36))
        "housing", "rent" -> Triple(Icons.Default.Home, Color(0xFFE9DDFF), Color(0xFF22005D))
        "bills & utilities", "utilities" -> Triple(Icons.Default.Lightbulb, Color(0xFFFFDEA1), Color(0xFF261A00))
        "entertainment" -> Triple(Icons.Default.Movie, Color(0xFFE0E0FF), Color(0xFF00006E))
        else -> Triple(Icons.Default.Receipt, Color(0xFFF1F3FB), Color(0xFF41484D))
    }

    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)
    val dateStr = dateFormat.format(Date(transaction.dateTimestamp))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("transaction_item_${transaction.id}")
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, ProfessionalBorder, RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = transaction.category,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = transaction.merchantName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = dateStr,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMutedSubtle,
                            fontSize = 12.sp
                        )

                        if (transaction.status == TransactionStatus.PENDING) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(PolishPendingYellow.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Pending",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PolishPendingYellow,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = if (isExpense) "-$formattedAmount" else "+$formattedAmount",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isExpense) PolishExpenseRed else PolishIncomeGreen
            )
        }
    }
}
