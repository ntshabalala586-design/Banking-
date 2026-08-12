package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionStatus {
    PENDING,
    CLEARED
}

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey val id: String,
    val accountId: String,
    val merchantName: String,
    val amount: Double, // Negative for expense, positive for income
    val category: String,
    val dateTimestamp: Long,
    val status: TransactionStatus = TransactionStatus.CLEARED,
    val note: String = "",
    val isManual: Boolean = false
)
