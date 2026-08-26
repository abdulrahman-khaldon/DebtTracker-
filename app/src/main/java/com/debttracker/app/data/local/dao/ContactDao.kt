package com.debttracker.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.debttracker.app.data.local.ContactEntity
import com.debttracker.app.data.local.ContactWithBalance
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Insert
    suspend fun insert(contact: ContactEntity): Long

    @Update
    suspend fun update(contact: ContactEntity)

    @Delete
    suspend fun delete(contact: ContactEntity)

    @Query("DELETE FROM contacts WHERE id = :contactId")
    suspend fun deleteById(contactId: Long)

    @Query("SELECT * FROM contacts WHERE id = :contactId")
    suspend fun getContactById(contactId: Long): ContactEntity?

    @Query("SELECT * FROM contacts ORDER BY name ASC")
    suspend fun getAllOnce(): List<ContactEntity>

    /**
     * All contacts with their net balance (unsettled transactions only),
     * sorted by the absolute balance so the biggest relationships come first.
     */
    @Query(
        """
        SELECT c.id AS id, c.name AS name, c.phone AS phone, c.createdAt AS createdAt,
            COALESCE(SUM(CASE WHEN t.type = 'CREDIT' THEN t.amount ELSE -t.amount END), 0) AS balance,
            (SELECT MAX(t2.date) FROM transactions t2 WHERE t2.contactId = c.id) AS lastTransactionDate
        FROM contacts c
        LEFT JOIN transactions t ON t.contactId = c.id AND t.isSettled = 0
        GROUP BY c.id
        ORDER BY ABS(balance) DESC, c.name ASC
        """
    )
    fun getContactsWithBalance(): Flow<List<ContactWithBalance>>

    @Query(
        """
        SELECT c.id AS id, c.name AS name, c.phone AS phone, c.createdAt AS createdAt,
            COALESCE(SUM(CASE WHEN t.type = 'CREDIT' THEN t.amount ELSE -t.amount END), 0) AS balance,
            (SELECT MAX(t2.date) FROM transactions t2 WHERE t2.contactId = c.id) AS lastTransactionDate
        FROM contacts c
        LEFT JOIN transactions t ON t.contactId = c.id AND t.isSettled = 0
        WHERE c.id = :contactId
        GROUP BY c.id
        """
    )
    fun getContactWithBalance(contactId: Long): Flow<ContactWithBalance?>

    @Query("SELECT COUNT(*) FROM contacts")
    suspend fun count(): Int

    @Query("DELETE FROM contacts")
    suspend fun deleteAll()
}
