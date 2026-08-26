package com.debttracker.app.data.csv

import com.debttracker.app.data.local.ContactEntity
import com.debttracker.app.data.local.TransactionEntity
import com.debttracker.app.data.local.TransactionType
import com.debttracker.app.data.repository.DebtRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class CsvExporterTest {

    @Test
    fun testCsvGenerationWithBomAndEscaping() = runBlocking {
        val testContact = ContactEntity(id = 1L, name = "أحمد, علي", phone = "0501234567")
        val todayMillis = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val testTransaction = TransactionEntity(
            id = 10L,
            contactId = 1L,
            amount = 1500.5,
            type = TransactionType.CREDIT,
            date = todayMillis,
            notes = "سلفة \"خاصة\"",
            isSettled = false
        )

        // Mock fake data repository
        val mockRepo = object : DebtRepository(
            contactDao = throw UnsupportedOperationException(),
            transactionDao = throw UnsupportedOperationException()
        ) {
            override suspend fun getAllContactsOnce(): List<ContactEntity> = listOf(testContact)
            override suspend fun getAllTransactionsOnce(): List<TransactionEntity> = listOf(testTransaction)
        }

        val exporter = CsvExporter(mockRepo)
        val csv = exporter.build()

        assertTrue(csv.startsWith("\uFEFF"))
        assertTrue(csv.contains("Contact,Phone,Date,Type,Amount,Notes,Settled"))
        assertTrue(csv.contains("\"أحمد, علي\""))
        assertTrue(csv.contains("Owed to me"))
        assertTrue(csv.contains("1500.5"))
        assertTrue(csv.contains("\"سلفة \"\"خاصة\"\"\""))
    }
}
