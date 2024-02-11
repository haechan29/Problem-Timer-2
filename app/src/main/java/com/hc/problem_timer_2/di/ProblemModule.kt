package com.hc.problem_timer_2.di

import android.content.Context
import androidx.room.Room
import com.hc.problem_timer_2.dao.BookDao
import com.hc.problem_timer_2.dao.ProblemDao
import com.hc.problem_timer_2.datasource.BookDB
import com.hc.problem_timer_2.datasource.ProblemDB
import com.hc.problem_timer_2.repository.BookRepository
import com.hc.problem_timer_2.repository.ProblemRepository
import com.hc.problem_timer_2.repository_impl.BookRepositoryImpl
import com.hc.problem_timer_2.repository_impl.ProblemRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ProblemModule {
    @Provides
    @Singleton
    fun provideProblemDB(@ApplicationContext context: Context): ProblemDB {
        return Room.databaseBuilder(
            context.applicationContext,
            ProblemDB::class.java,
            "problem database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideProblemDao(problemDB: ProblemDB): ProblemDao {
        return problemDB.problemDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ProblemRepositoryModule {
    @Binds
    abstract fun bindProblemRepository(problemRepositoryImpl: ProblemRepositoryImpl): ProblemRepository
}