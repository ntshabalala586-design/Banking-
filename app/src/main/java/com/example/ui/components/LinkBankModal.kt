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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.AccountType
import com.example.ui.theme.ProfessionalBorder
import com.example.ui.theme.ProfessionalPrimary
import com.example.ui.theme.TextMutedSubtle
import com.example.ui.theme.TextPrimaryDark

data class PopularInstitution(
    val name: String,
    val hexColor: String,
    val defaultType: AccountType
)

val PopularInstitutionsList = listOf(
    PopularInstitution("Chase", "#117ACA", AccountType.CHECKING),
    PopularInstitution("Bank of America", "#E31837", AccountType.SAVINGS),
    PopularInstitution("Fidelity", "#328332", AccountType.INVESTMENT),
    PopularInstitution("Capital One", "#004977", AccountType.CREDIT_CARD),
    PopularInstitution("Wells Fargo", "#CD1409", AccountType.CHECKING),
    PopularInstitution("Citi", "#003B70", AccountType.CREDIT_CARD)
)

@Composable
fun LinkBankModal(
    onDismiss: () -> Unit,
    onLinkBank: (institutionName: String, accountName: String, accountType: AccountType, balance: Double, colorHex: String) -> Unit
) {
    var selectedInst by remember { mutableStateOf(PopularInstitutionsList[0]) }
    var accountName by remember { mutableStateOf("Checking Account") }
    var accountType by remember { mutableStateOf(AccountType.CHECKING) }
    var balanceText by remember { mutableStateOf("2500.00") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("link_bank_dialog")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Connect Bank Account",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "Select your financial institution to enable real-time balance tracking and direct sync.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMutedSubtle
                )

                // Institution Chips
                Text(
                    text = "Institution",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(PopularInstitutionsList) { inst ->
                        val isSelected = inst.name == selectedInst.name
                        val instColor = try {
                            Color(android.graphics.Color.parseColor(inst.hexColor))
                        } catch (e: Exception) {
                            ProfessionalPrimary
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) instColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface)
                                .border(
                                    1.2.dp,
                                    if (isSelected) instColor else ProfessionalBorder,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    selectedInst = inst
                                    accountType = inst.defaultType
                                    accountName = when (inst.defaultType) {
                                        AccountType.CHECKING -> "Checking"
                                        AccountType.SAVINGS -> "Savings"
                                        AccountType.CREDIT_CARD -> "Credit Card"
                                        AccountType.INVESTMENT -> "Investment"
                                    }
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(instColor)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = inst.name,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = TextPrimaryDark
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = accountName,
                    onValueChange = { accountName = it },
                    label = { Text("Account Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("link_bank_account_name_input")
                )

                OutlinedTextField(
                    value = balanceText,
                    onValueChange = { balanceText = it },
                    label = { Text("Initial Balance ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("link_bank_balance_input")
                )

                Button(
                    onClick = {
                        val bal = balanceText.toDoubleOrNull() ?: 0.0
                        onLinkBank(
                            selectedInst.name,
                            accountName.ifBlank { "Checking" },
                            accountType,
                            bal,
                            selectedInst.hexColor
                        )
                        onDismiss()
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ProfessionalPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("confirm_link_bank_button")
                ) {
                    Icon(imageVector = Icons.Default.AccountBalance, contentDescription = "Connect")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Connect & Sync Now", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
