package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BankAccount
import com.example.ui.components.BankAccountCard
import com.example.ui.components.SpendingBarChart
import com.example.ui.components.TransactionItem
import com.example.ui.theme.PolishIncomeGreen
import com.example.ui.theme.ProfessionalBorder
import com.example.ui.theme.ProfessionalOutline
import com.example.ui.theme.ProfessionalPrimary
import com.example.ui.theme.ProfessionalPrimaryContainer
import com.example.ui.theme.TextMutedSubtle
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.viewmodel.FinanceViewModel
import com.example.ui.viewmodel.SyncState
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: FinanceViewModel,
    onNavigateToAccounts: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    onAddTransactionClick: () -> Unit,
    onLinkBankClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accounts by viewModel.accounts.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val syncState by viewModel.syncState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(syncState) {
        when (val state = syncState) {
            is SyncState.Success -> {
                snackbarHostState.showSnackbar("Bank sync updated! ${state.count} live transaction(s) fetched.")
                viewModel.resetSyncState()
            }
            is SyncState.Error -> {
                snackbarHostState.showSnackbar("Sync notice: ${state.message}")
                viewModel.resetSyncState()
            }
            else -> {}
        }
    }

    val totalNetWorth = accounts.sumOf { it.balance }
    val usdFormat = NumberFormat.getCurrencyInstance(Locale.US)

    // Calculate monthly budget progress
    val now = System.currentTimeMillis()
    val thirtyDaysAgo = now - (30 * 86400000L)
    val monthlyExpenses = transactions.filter { it.dateTimestamp >= thirtyDaysAgo && it.amount < 0 }
        .sumOf { Math.abs(it.amount) }
    val monthlyBudgetLimit = 3500.0
    val budgetProgress = (monthlyExpenses / monthlyBudgetLimit).coerceIn(0.0, 1.0).toFloat()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Top Header (Match Professional Polish Header)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(ProfessionalPrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "User Avatar",
                                tint = Color(0xFF001D36),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Welcome back,",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMutedSubtle
                            )
                            Text(
                                text = "Alex Rivera",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                        }
                    }

                    // Bank Sync Action
                    IconButton(
                        onClick = { viewModel.syncBankData() },
                        enabled = syncState !is SyncState.Syncing,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, ProfessionalBorder, CircleShape)
                            .testTag("sync_now_header_button")
                    ) {
                        if (syncState is SyncState.Syncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = ProfessionalPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Sync Bank Account",
                                tint = ProfessionalPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Hero Balance Card (Reflects Professional Polish #D3E4FF Card)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("total_balance_hero_card"),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = ProfessionalPrimaryContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total Net Worth",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF001D36).copy(alpha = 0.8f)
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF004A77))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "+Live Sync",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = usdFormat.format(totalNetWorth),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF001D36),
                            fontSize = 32.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Budget Progress Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFF001D36).copy(alpha = 0.12f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(budgetProgress)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color(0xFF001D36))
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Monthly budget: ${(budgetProgress * 100).toInt()}% utilized (${usdFormat.format(monthlyExpenses)} spent)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF001D36).copy(alpha = 0.75f),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Quick Actions 4-Grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    QuickActionItem(
                        icon = Icons.Default.Send,
                        label = "Add Tx",
                        onClick = onAddTransactionClick,
                        tag = "quick_action_add_tx"
                    )
                    QuickActionItem(
                        icon = Icons.Default.Payments,
                        label = "Link Bank",
                        onClick = onLinkBankClick,
                        tag = "quick_action_link_bank"
                    )
                    QuickActionItem(
                        icon = Icons.Default.AccountBalanceWallet,
                        label = "Accounts",
                        onClick = onNavigateToAccounts,
                        tag = "quick_action_accounts"
                    )
                    QuickActionItem(
                        icon = Icons.Default.MoreHoriz,
                        label = "Sync",
                        onClick = { viewModel.syncBankData() },
                        tag = "quick_action_sync"
                    )
                }
            }

            // Weekly Spending Insights Bar Chart
            item {
                SpendingBarChart(transactions = transactions)
            }

            // Linked Accounts Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Linked Bank Accounts (${accounts.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )

                    TextButton(onClick = onNavigateToAccounts) {
                        Text(
                            text = "Manage",
                            fontWeight = FontWeight.Bold,
                            color = ProfessionalPrimary
                        )
                    }
                }
            }

            item {
                if (accounts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No bank accounts linked. Click 'Link Bank' above.")
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(accounts) { acc ->
                            Box(modifier = Modifier.width(280.dp)) {
                                BankAccountCard(account = acc, onClick = onNavigateToAccounts)
                            }
                        }
                    }
                }
            }

            // Recent Transactions Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Transactions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )

                    TextButton(onClick = onNavigateToTransactions) {
                        Text(
                            text = "See all",
                            fontWeight = FontWeight.Bold,
                            color = ProfessionalPrimary
                        )
                    }
                }
            }

            items(transactions.take(5)) { tx ->
                TransactionItem(transaction = tx, onClick = onNavigateToTransactions)
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun QuickActionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    tag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .testTag(tag)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, ProfessionalOutline, RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = ProfessionalPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextMutedSubtle,
            fontSize = 11.sp
        )
    }
}
