package com.debttracker.app.data.csv

import com.debttracker.app.data.local.TransactionEntity
import com.debttracker.app.data.local.TransactionType
import com.debttracker.app.data.repository.DebtRepository
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Builds a CSV document of every contact and transaction.
 * A UTF-8 BOM is prepended so Arabic text opens correctly in spreadsheet apps.
 */
@Singleton
class CsvExporter @Inject constructor(
    private val repository: DebtRepository
) {

    suspend fun build(): String {
        val contactsById = repository.getAllContactsOnce().associateBy { it.id }
        val transactions = repository.getAllTransactionsOnce()
        val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

        return buildString {
            append(BOM)
            appendLine("Contact,Phone,Date,Type,Amount,Notes,Settled")
            transactions.forEach { transaction ->
                val contact = contactsById[transaction.contactId]
                val row = listOf(
                    contact?.name ?: "Unknown",
                    contact?.phone.orEmpty(),
                    dateFormatter.format(
                        Instant.ofEpochMilli(transaction.date).atZone(ZoneId.systemDefault()).toLocalDate()
                    ),
                    typeName(transaction.type),
                    formatAmount(transaction.amount),
                    transaction.notes.orEmpty(),
                    if (transaction.isSettled) "yes" else "no"
                )
                appendLine(row.joinToString(",") { escape(it) })
            }
        }
    }

    private fun typeName(type: TransactionType): String =
        if (type == TransactionType.CREDIT) "Owed to me" else "I owe"

    private fun formatAmount(amount: Double): String =
        if (amount == amount.toLong().toDouble()) {
            amount.toLong().toString()
        } else {
            amount.toString()
        }

    private fun escape(field: String): String =
        if (field.any { it == ',' || it == '"' || it == '\n' || it == '\r' }) {
            "\"${field.replace("\"", "\"\"")}\""
        } else {
            field
        }

    private companion object {
        const val BOM = "\uFEFF"
    }
}
