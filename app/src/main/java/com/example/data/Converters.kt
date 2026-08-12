package com.example.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromAccountType(type: AccountType): String = type.name

    @TypeConverter
    fun toAccountType(value: String): AccountType = try {
        AccountType.valueOf(value)
    } catch (e: Exception) {
        AccountType.CHECKING
    }

    @TypeConverter
    fun fromTransactionStatus(status: TransactionStatus): String = status.name

    @TypeConverter
    fun toTransactionStatus(value: String): TransactionStatus = try {
        TransactionStatus.valueOf(value)
    } catch (e: Exception) {
        TransactionStatus.CLEARED
    }
}
