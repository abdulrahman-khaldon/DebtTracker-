package com.debttracker.app.data.repository

import com.debttracker.app.data.local.ContactEntity
import com.debttracker.app.data.local.ContactWithBalance
import com.debttracker.app.data.local.GlobalTotals
import com.debttracker.app.data.local.TransactionEntity
import com.debttracker.app.data.local.dao.ContactDao
import com.debttracker.app.data.local.dao.TransactionDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.map

@Singleton
open class DebtRepository @Inject constructor(
    private val contactDao: ContactDao,
    private val transactionDao: TransactionDao
) {

    val contactsWithBalance: Flow<List<ContactWithBalance>> = contactDao.getContactsWithBalance()

    val globalTotals: Flow<GlobalTotals> = transactionDao.getGlobalTotals()
        .map { it ?: GlobalTotals(0.0, 0.0) }

    fun contactWithBalance(contactId: Long): Flow<ContactWithBalance?> =
        contactDao.getContactWithBalance(contactId)

    fun transactionsForContact(contactId: Long): Flow<List<TransactionEntity>> =
        transactionDao.getTransactionsForContact(contactId)

    suspend fun getContact(contactId: Long): ContactEntity? = contactDao.getContactById(contactId)

    suspend fun addContact(name: String, phone: String?): Long =
        contactDao.insert(
            ContactEntity(
                name = name.trim(),
                phone = phone?.trim()?.ifEmpty { null }
            )
        )

    suspend fun updateContact(contact: ContactEntity) =
        contactDao.update(contact.copy(name = contact.name.trim(), phone = contact.phone?.trim()?.ifEmpty { null }))

    suspend fun deleteContact(contactId: Long) = contactDao.deleteById(contactId)

    suspend fun addTransaction(transaction: TransactionEntity): Long =
        transactionDao.insert(transaction)

    suspend fun updateTransaction(transaction: TransactionEntity) =
        transactionDao.update(transaction)

    suspend fun deleteTransaction(transaction: TransactionEntity) =
        transactionDao.delete(transaction)

    suspend fun settleAllForContact(contactId: Long) =
        transactionDao.settleAllForContact(contactId)

    open suspend fun getAllContactsOnce(): List<ContactEntity> = contactDao.getAllOnce()

    open suspend fun getAllTransactionsOnce(): List<TransactionEntity> = transactionDao.getAllOnce()

    suspend fun clearAll() {
        transactionDao.deleteAll()
        contactDao.deleteAll()
    }
}
