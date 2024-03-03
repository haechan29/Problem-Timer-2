package com.hc.problem_timer_2.data.di

import android.content.Context
import androidx.room.Room
import com.hc.problem_timer_2.data.dao.ProblemRecordDao
import com.hc.problem_timer_2.data.datasource.ProblemRecordDB
import com.hc.problem_timer_2.data.repository.ProblemRecordRepository
import com.hc.problem_timer_2.data.repository_impl.ProblemRecordRepositoryImpl
import dagger.Binds
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

@Module
@InstallIn(SingletonComponent::class)
abstract class ProblemRecordRepositoryModule {
    @Binds
    abstract fun bindProblemRecordRepository(problemRecordRepositoryImpl: ProblemRecordRepositoryImpl): ProblemRecordRepository
}