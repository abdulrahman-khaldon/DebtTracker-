package com.debttracker.app.data.local

/**
 * Global totals across all contacts (per-contact netting):
 * a contact with a positive balance contributes to [totalOwedToMe],
 * a contact with a negative balance contributes to [totalIOwe].
 */
data class GlobalTotals(
    val totalOwedToMe: Double = 0.0,
    val totalIOwe: Double = 0.0
) {
    val net: Double get() = totalOwedToMe - totalIOwe
}
