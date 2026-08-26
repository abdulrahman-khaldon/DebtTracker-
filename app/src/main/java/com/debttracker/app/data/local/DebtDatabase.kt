package com.debttracker.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.debttracker.app.data.local.dao.ContactDao
import com.debttracker.app.data.local.dao.TransactionDao

@Database(
    entities = [ContactEntity::class, TransactionEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class DebtDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun transactionDao(): TransactionDao
}
