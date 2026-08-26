package com.debttracker.app.data.local

/**
 * Contact row with its net balance (credits minus debts of unsettled transactions)
 * and the date of its most recent transaction (settled ones included).
 */
data class ContactWithBalance(
    val id: Long,
    val name: String,
    val phone: String?,
    val createdAt: Long,
    val balance: Double,
    val lastTransactionDate: Long?
)
