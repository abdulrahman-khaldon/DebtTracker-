package com.debttracker.app.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = ContactEntity::class,
            parentColumns = ["id"],
            childColumns = ["contactId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("contactId")]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val contactId: Long,
    val amount: Double,
    val type: TransactionType,
    val date: Long,
    val notes: String? = null,
    val isSettled: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
