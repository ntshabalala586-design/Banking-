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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShowChart
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AccountType
import com.example.data.BankAccount
import com.example.ui.theme.PolishExpenseRed
import com.example.ui.theme.PolishIncomeGreen
import com.example.ui.theme.ProfessionalBorder
import com.example.ui.theme.ProfessionalPrimaryContainer
import com.example.ui.theme.TextMutedSubtle
import com.example.ui.theme.TextPrimaryDark
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BankAccountCard(
    account: BankAccount,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val usdFormat = NumberFormat.getCurrencyInstance(Locale.US)
    val colorHex = try {
        Color(android.graphics.Color.parseColor(account.institutionColorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    val icon = when (account.accountType) {
        AccountType.CHECKING -> Icons.Default.AccountBalance
        AccountType.SAVINGS -> Icons.Default.Savings
        AccountType.CREDIT_CARD -> Icons.Default.CreditCard
        AccountType.INVESTMENT -> Icons.Default.ShowChart
    }

    val typeLabel = when (account.accountType) {
        AccountType.CHECKING -> "Checking"
        AccountType.SAVINGS -> "Savings"
        AccountType.CREDIT_CARD -> "Credit"
        AccountType.INVESTMENT -> "Investment"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("bank_account_card_${account.id}")
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, ProfessionalBorder, RoundedCornerShape(24.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(colorHex.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = account.institutionName,
                            tint = colorHex,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = account.institutionName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                        Text(
                            text = "${account.accountName} • ${account.accountNumberMasked}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMutedSubtle,
                            fontSize = 12.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(ProfessionalPrimaryContainer)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = typeLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF001D36),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Current Balance",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMutedSubtle
                    )
                    Text(
                        text = usdFormat.format(account.balance),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (account.balance < 0) PolishExpenseRed else TextPrimaryDark
                    )
                }

                val timeFormat = SimpleDateFormat("h:mm a", Locale.US)
                val timeStr = timeFormat.format(Date(account.lastSyncedTimestamp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(PolishIncomeGreen)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Synced $timeStr",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMutedSubtle,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
