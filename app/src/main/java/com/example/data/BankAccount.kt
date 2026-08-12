package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AccountType {
    CHECKING,
    SAVINGS,
    CREDIT_CARD,
    INVESTMENT
}

@Entity(tableName = "bank_accounts")
data class BankAccount(
    @PrimaryKey val id: String,
    val institutionName: String,
    val accountName: String,
    val accountType: AccountType,
    val accountNumberMasked: String,
    val balance: Double,
    val currency: String = "USD",
    val lastSyncedTimestamp: Long = System.currentTimeMillis(),
    val isLinked: Boolean = true,
    val institutionColorHex: String = "#0284C7"
)
