package com.hc.problem_timer_2.di

import android.content.Context
import androidx.room.Room
import com.hc.problem_timer_2.dao.BookDao
import com.hc.problem_timer_2.dao.ProblemRecordDao
import com.hc.problem_timer_2.datasource.BookDB
import com.hc.problem_timer_2.datasource.ProblemRecordDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ProblemRecordDBModule {
    @Provides
    @Singleton
    fun provideProblemRecordDB(@ApplicationContext context: Context): ProblemRecordDB {
        return Room.databaseBuilder(
            context.applicationContext,
            ProblemRecordDB::class.java,
            "problem record database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideProblemRecordDao(problemRecordDB: ProblemRecordDB): ProblemRecordDao {
        return problemRecordDB.problemRecordDao()
    }
}