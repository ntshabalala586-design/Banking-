package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AddTransactionModal
import com.example.ui.components.LinkBankModal
import com.example.ui.theme.ProfessionalBorder
import com.example.ui.theme.ProfessionalPrimary
import com.example.ui.theme.ProfessionalPrimaryContainer
import com.example.ui.theme.TextMutedSubtle
import com.example.ui.viewmodel.FinanceViewModel

enum class MainTab {
    HOME,
    INSIGHTS,
    ACCOUNTS,
    TRANSACTIONS
}

@Composable
fun MainAppScreen(
    viewModel: FinanceViewModel = viewModel()
) {
    var selectedTab by remember { mutableStateOf(MainTab.HOME) }
    var showLinkBankModal by remember { mutableStateOf(false) }
    var showAddTxModal by remember { mutableStateOf(false) }

    val accounts by viewModel.accounts.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .border(1.dp, ProfessionalBorder)
                    .testTag("bottom_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = selectedTab == MainTab.HOME,
                    onClick = { selectedTab = MainTab.HOME },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Home",
                            fontWeight = if (selectedTab == MainTab.HOME) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 10.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ProfessionalPrimary,
                        selectedTextColor = ProfessionalPrimary,
                        unselectedIconColor = TextMutedSubtle,
                        unselectedTextColor = TextMutedSubtle,
                        indicatorColor = ProfessionalPrimaryContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_home")
                )

                NavigationBarItem(
                    selected = selectedTab == MainTab.INSIGHTS,
                    onClick = { selectedTab = MainTab.INSIGHTS },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Insights,
                            contentDescription = "Insights",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Insights",
                            fontWeight = if (selectedTab == MainTab.INSIGHTS) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 10.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ProfessionalPrimary,
                        selectedTextColor = ProfessionalPrimary,
                        unselectedIconColor = TextMutedSubtle,
                        unselectedTextColor = TextMutedSubtle,
                        indicatorColor = ProfessionalPrimaryContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_insights")
                )

                NavigationBarItem(
                    selected = selectedTab == MainTab.ACCOUNTS,
                    onClick = { selectedTab = MainTab.ACCOUNTS },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = "Accounts",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Accounts",
                            fontWeight = if (selectedTab == MainTab.ACCOUNTS) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 10.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ProfessionalPrimary,
                        selectedTextColor = ProfessionalPrimary,
                        unselectedIconColor = TextMutedSubtle,
                        unselectedTextColor = TextMutedSubtle,
                        indicatorColor = ProfessionalPrimaryContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_accounts")
                )

                NavigationBarItem(
                    selected = selectedTab == MainTab.TRANSACTIONS,
                    onClick = { selectedTab = MainTab.TRANSACTIONS },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = "Transactions",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Activity",
                            fontWeight = if (selectedTab == MainTab.TRANSACTIONS) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 10.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ProfessionalPrimary,
                        selectedTextColor = ProfessionalPrimary,
                        unselectedIconColor = TextMutedSubtle,
                        unselectedTextColor = TextMutedSubtle,
                        indicatorColor = ProfessionalPrimaryContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_transactions")
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedTab) {
                MainTab.HOME -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToAccounts = { selectedTab = MainTab.ACCOUNTS },
                    onNavigateToTransactions = { selectedTab = MainTab.TRANSACTIONS },
                    onAddTransactionClick = { showAddTxModal = true },
                    onLinkBankClick = { showLinkBankModal = true }
                )
                MainTab.INSIGHTS -> InsightsScreen(viewModel = viewModel)
                MainTab.ACCOUNTS -> AccountsScreen(
                    viewModel = viewModel,
                    onOpenLinkBankModal = { showLinkBankModal = true }
                )
                MainTab.TRANSACTIONS -> TransactionsScreen(
                    viewModel = viewModel,
                    onOpenAddTransactionModal = { showAddTxModal = true }
                )
            }
        }
    }

    if (showLinkBankModal) {
        LinkBankModal(
            onDismiss = { showLinkBankModal = false },
            onLinkBank = { name, accName, type, bal, color ->
                viewModel.linkNewBank(name, accName, type, bal, color)
            }
        )
    }

    if (showAddTxModal) {
        AddTransactionModal(
            accounts = accounts,
            onDismiss = { showAddTxModal = false },
            onAddTransaction = { tx ->
                viewModel.addManualTransaction(tx)
            }
        )
    }
}
