package com.debttracker.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.debttracker.app.data.local.GlobalTotals
import com.debttracker.app.data.local.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert
    suspend fun insert(transaction: TransactionEntity): Long

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE contactId = :contactId ORDER BY date DESC, createdAt DESC")
    fun getTransactionsForContact(contactId: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    suspend fun getAllOnce(): List<TransactionEntity>

    /** Marks every unsettled transaction of the contact as settled; history is kept. */
    @Query("UPDATE transactions SET isSettled = 1 WHERE contactId = :contactId AND isSettled = 0")
    suspend fun settleAllForContact(contactId: Long)

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()

    /**
     * Global totals with per-contact netting: contacts whose balance is positive
     * count towards totalOwedToMe, the rest towards totalIOwe.
     */
    @Query(
        """
        SELECT
            COALESCE(SUM(CASE WHEN balance > 0 THEN balance ELSE 0 END), 0) AS totalOwedToMe,
            COALESCE(SUM(CASE WHEN balance < 0 THEN -balance ELSE 0 END), 0) AS totalIOwe
        FROM (
            SELECT COALESCE(SUM(CASE WHEN t.type = 'CREDIT' THEN t.amount ELSE -t.amount END), 0) AS balance
            FROM contacts c
            LEFT JOIN transactions t ON t.contactId = c.id AND t.isSettled = 0
            GROUP BY c.id
        )
        """
    )
    fun getGlobalTotals(): Flow<GlobalTotals?>
}
