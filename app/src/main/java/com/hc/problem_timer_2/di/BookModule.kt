package com.hc.problem_timer_2.di

import android.content.Context
import androidx.room.Room
import com.hc.problem_timer_2.datasource.BookDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class BookModule {
    @Provides
    @Singleton
    fun provideBookDB(@ApplicationContext context: Context): BookDB {
        return Room.databaseBuilder(
            context.applicationContext,
            BookDB::class.java,
            "book database"
        ).build()
    }
}