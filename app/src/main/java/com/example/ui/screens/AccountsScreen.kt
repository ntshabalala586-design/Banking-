package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.BankAccount
import com.example.ui.components.BankAccountCard
import com.example.ui.theme.PolishExpenseRed
import com.example.ui.theme.ProfessionalPrimary
import com.example.ui.theme.TextMutedSubtle
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.viewmodel.FinanceViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AccountsScreen(
    viewModel: FinanceViewModel,
    onOpenLinkBankModal: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accounts by viewModel.accounts.collectAsState()
    var accountToUnlink by remember { mutableStateOf<BankAccount?>(null) }
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Linked Bank Accounts",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                        Text(
                            text = "Real-time sync & balance monitoring",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMutedSubtle
                        )
                    }

                    Button(
                        onClick = onOpenLinkBankModal,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ProfessionalPrimary),
                        modifier = Modifier.testTag("link_new_bank_header_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Connect", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            items(accounts) { acc ->
                Box {
                    BankAccountCard(account = acc)

                    // Unlink icon on top right
                    IconButton(
                        onClick = { accountToUnlink = acc },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Unlink Bank",
                            tint = PolishExpenseRed.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            if (accounts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No bank accounts linked yet.", color = TextMutedSubtle)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    accountToUnlink?.let { acc ->
        AlertDialog(
            onDismissRequest = { accountToUnlink = null },
            title = { Text("Unlink Bank Account?") },
            text = { Text("Are you sure you want to disconnect ${acc.institutionName} (${acc.accountName})? Live syncing will be suspended.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.unlinkAccount(acc.id)
                        accountToUnlink = null
                    }
                ) {
                    Text("Unlink", color = PolishExpenseRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { accountToUnlink = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
