package com.debttracker.app.di

import android.content.Context
import androidx.room.Room
import com.debttracker.app.data.local.DebtDatabase
import com.debttracker.app.data.local.dao.ContactDao
import com.debttracker.app.data.local.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DATABASE_NAME = "debt_tracker.db"

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DebtDatabase =
        Room.databaseBuilder(
            context,
            DebtDatabase::class.java,
            DATABASE_NAME
        ).build()

    @Provides
    fun provideContactDao(database: DebtDatabase): ContactDao = database.contactDao()

    @Provides
    fun provideTransactionDao(database: DebtDatabase): TransactionDao = database.transactionDao()
}
