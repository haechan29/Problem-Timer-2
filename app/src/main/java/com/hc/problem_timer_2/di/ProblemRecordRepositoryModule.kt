package com.hc.problem_timer_2.di

import com.hc.problem_timer_2.repository.BookRepository
import com.hc.problem_timer_2.repository.ProblemRecordRepository
import com.hc.problem_timer_2.repository_impl.BookRepositoryImpl
import com.hc.problem_timer_2.repository_impl.ProblemRecordRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ProblemRecordRepositoryModule {
    @Binds
    abstract fun bindProblemRecordRepository(problemRecordRepositoryImpl: ProblemRecordRepositoryImpl): ProblemRecordRepository
}