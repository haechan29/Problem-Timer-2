package com.hc.problem_timer_2.di

import android.content.Context
import androidx.room.Room
import com.hc.problem_timer_2.dao.BookDao
import com.hc.problem_timer_2.datasource.BookDB
import com.hc.problem_timer_2.repository.BookRepository
import com.hc.problem_timer_2.repository_impl.BookRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class BookDBModule {
    @Provides
    @Singleton
    fun provideBookDB(@ApplicationContext context: Context): BookDB {
        return Room.databaseBuilder(
            context.applicationContext,
            BookDB::class.java,
            "book database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideBookDao(bookDB: BookDB): BookDao {
        return bookDB.bookDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class BookRepositoryModule {
    @Binds
    abstract fun bindBookRepository(bookRepositoryImpl: BookRepositoryImpl): BookRepository
}