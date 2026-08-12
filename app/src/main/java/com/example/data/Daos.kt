package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BankAccountDao {
    @Query("SELECT * FROM bank_accounts ORDER BY lastSyncedTimestamp DESC")
    fun getAllAccounts(): Flow<List<BankAccount>>

    @Query("SELECT * FROM bank_accounts WHERE id = :id")
    suspend fun getAccountById(id: String): BankAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: BankAccount)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccounts(accounts: List<BankAccount>)

    @Update
    suspend fun updateAccount(account: BankAccount)

    @Query("DELETE FROM bank_accounts WHERE id = :id")
    suspend fun deleteAccountById(id: String)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY dateTimestamp DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE accountId = :accountId ORDER BY dateTimestamp DESC")
    fun getTransactionsForAccount(accountId: String): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<Transaction>)

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: String)

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()
}

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets")
    fun getAllBudgets(): Flow<List<Budget>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: Budget)

    @Query("DELETE FROM budgets WHERE category = :category")
    suspend fun deleteBudget(category: String)
}
