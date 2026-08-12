package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AccountType
import com.example.data.BankAccount
import com.example.data.Budget
import com.example.data.FinanceRepository
import com.example.data.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class SyncState {
    object Idle : SyncState()
    object Syncing : SyncState()
    data class Success(val count: Int, val message: String) : SyncState()
    data class Error(val message: String) : SyncState()
}

class FinanceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FinanceRepository(application)

    val accounts: StateFlow<List<BankAccount>> = repository.accounts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val transactions: StateFlow<List<Transaction>> = repository.transactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val budgets: StateFlow<List<Budget>> = repository.budgets
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow("All")
    val selectedCategoryFilter: StateFlow<String> = _selectedCategoryFilter.asStateFlow()

    private val _selectedAccountFilter = MutableStateFlow("All")
    val selectedAccountFilter: StateFlow<String> = _selectedAccountFilter.asStateFlow()

    val filteredTransactions: StateFlow<List<Transaction>> = combine(
        transactions,
        searchQuery,
        selectedCategoryFilter,
        selectedAccountFilter
    ) { txs, query, category, accountId ->
        txs.filter { tx ->
            val matchesQuery = query.isBlank() || tx.merchantName.contains(query, ignoreCase = true) || tx.note.contains(query, ignoreCase = true)
            val matchesCategory = category == "All" || tx.category.equals(category, ignoreCase = true)
            val matchesAccount = accountId == "All" || tx.accountId == accountId
            matchesQuery && matchesCategory && matchesAccount
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            repository.initializeDefaultDataIfNeeded()
        }
    }

    fun syncBankData(customApiUrl: String? = null, apiKey: String? = null) {
        viewModelScope.launch {
            _syncState.value = SyncState.Syncing
            val result = repository.syncBankData(customApiUrl, apiKey)
            result.fold(
                onSuccess = { count ->
                    _syncState.value = SyncState.Success(count, "Bank account synced successfully")
                },
                onFailure = { error ->
                    _syncState.value = SyncState.Error(error.localizedMessage ?: "Sync failed")
                }
            )
        }
    }

    fun resetSyncState() {
        _syncState.value = SyncState.Idle
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(category: String) {
        _selectedCategoryFilter.value = category
    }

    fun setAccountFilter(accountId: String) {
        _selectedAccountFilter.value = accountId
    }

    fun linkNewBank(
        institutionName: String,
        accountName: String,
        accountType: AccountType,
        initialBalance: Double,
        institutionColorHex: String
    ) {
        viewModelScope.launch {
            repository.linkNewBank(
                institutionName,
                accountName,
                accountType,
                initialBalance,
                institutionColorHex
            )
            syncBankData()
        }
    }

    fun addManualTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.addTransaction(transaction)
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
        }
    }

    fun deleteTransaction(id: String) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
        }
    }

    fun setBudget(category: String, limit: Double) {
        viewModelScope.launch {
            repository.setBudget(category, limit)
        }
    }

    fun unlinkAccount(accountId: String) {
        viewModelScope.launch {
            repository.unlinkAccount(accountId)
        }
    }
}
