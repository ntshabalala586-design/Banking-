package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CategoriesList
import com.example.ui.components.SpendingBarChart
import com.example.ui.theme.PolishExpenseRed
import com.example.ui.theme.PolishIncomeGreen
import com.example.ui.theme.ProfessionalBorder
import com.example.ui.theme.ProfessionalPrimary
import com.example.ui.theme.ProfessionalPrimaryContainer
import com.example.ui.theme.TextMutedSubtle
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.viewmodel.FinanceViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun InsightsScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val transactions by viewModel.transactions.collectAsState()
    val budgets by viewModel.budgets.collectAsState()

    val now = System.currentTimeMillis()
    val thirtyDaysAgo = now - (30 * 86400000L)
    val monthlyTxs = transactions.filter { it.dateTimestamp >= thirtyDaysAgo }

    val categoryExpenses = CategoriesList.map { category ->
        val totalSpent = monthlyTxs
            .filter { it.category.equals(category, ignoreCase = true) && it.amount < 0 }
            .sumOf { Math.abs(it.amount) }

        val budgetLimit = budgets.firstOrNull { it.category.equals(category, ignoreCase = true) }?.monthlyLimit ?: 400.0
        Triple(category, totalSpent, budgetLimit)
    }.sortedByDescending { it.second }

    val totalSpent30Days = categoryExpenses.sumOf { it.second }
    val totalBudget30Days = categoryExpenses.sumOf { it.third }
    val usdFormat = NumberFormat.getCurrencyInstance(Locale.US)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                Column {
                    Text(
                        text = "Financial Insights",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    Text(
                        text = "Budget tracking & category breakdown",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMutedSubtle
                    )
                }
            }

            // Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = ProfessionalPrimaryContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Monthly Expenses",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF001D36).copy(alpha = 0.8f)
                            )
                            Text(
                                text = usdFormat.format(totalSpent30Days),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF001D36)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Budget Target",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF001D36).copy(alpha = 0.8f)
                            )
                            Text(
                                text = usdFormat.format(totalBudget30Days),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF004A77)
                            )
                        }
                    }
                }
            }

            // Weekly Chart
            item {
                SpendingBarChart(transactions = transactions)
            }

            item {
                Text(
                    text = "Category Budget Utilization",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
            }

            items(categoryExpenses) { (cat, spent, limit) ->
                val progress = (spent / limit).coerceIn(0.0, 1.0).toFloat()
                val isOver = spent > limit

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, ProfessionalBorder, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )

                            Text(
                                text = "${usdFormat.format(spent)} / ${usdFormat.format(limit)}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isOver) PolishExpenseRed else TextPrimaryDark
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFE1E2EC))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(progress)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isOver) PolishExpenseRed else ProfessionalPrimary)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${(progress * 100).toInt()}% utilized",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMutedSubtle,
                                fontSize = 11.sp
                            )

                            if (isOver) {
                                Text(
                                    text = "Over Budget",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PolishExpenseRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}
