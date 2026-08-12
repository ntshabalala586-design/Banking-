package com.example.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

@JsonClass(generateAdapter = true)
data class NetworkBankAccount(
    @Json(name = "account_id") val accountId: String,
    @Json(name = "institution") val institution: String,
    @Json(name = "name") val name: String,
    @Json(name = "type") val type: String, // checking, savings, credit, investment
    @Json(name = "mask") val mask: String,
    @Json(name = "current_balance") val currentBalance: Double,
    @Json(name = "currency") val currency: String = "USD"
)

@JsonClass(generateAdapter = true)
data class NetworkTransaction(
    @Json(name = "transaction_id") val transactionId: String,
    @Json(name = "account_id") val accountId: String,
    @Json(name = "merchant_name") val merchantName: String,
    @Json(name = "amount") val amount: Double,
    @Json(name = "category") val category: String,
    @Json(name = "date") val date: String,
    @Json(name = "pending") val pending: Boolean = false
)

@JsonClass(generateAdapter = true)
data class BankSyncResponse(
    @Json(name = "status") val status: String,
    @Json(name = "accounts") val accounts: List<NetworkBankAccount>,
    @Json(name = "transactions") val transactions: List<NetworkTransaction>
)

@JsonClass(generateAdapter = true)
data class LinkAccountRequest(
    @Json(name = "institution_id") val institutionId: String,
    @Json(name = "public_token") val publicToken: String,
    @Json(name = "client_id") val clientId: String? = null
)

interface BankApiService {
    @GET
    suspend fun syncBankData(
        @Url url: String,
        @Header("Authorization") authToken: String?
    ): Response<BankSyncResponse>

    @POST
    suspend fun linkInstitution(
        @Url url: String,
        @Body request: LinkAccountRequest
    ): Response<BankSyncResponse>
}
