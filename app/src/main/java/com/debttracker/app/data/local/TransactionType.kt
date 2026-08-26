package com.debttracker.app.data.local

/**
 * CREDIT: the contact owes me money (adds to the balance they owe me).
 * DEBT: I owe the contact money (subtracts from the balance).
 */
enum class TransactionType {
    CREDIT,
    DEBT
}
