package com.hc.problem_timer_2.repository_impl

import com.hc.problem_timer_2.dao.BookDao
import com.hc.problem_timer_2.dao.ProblemRecordDao
import com.hc.problem_timer_2.vo.Book
import com.hc.problem_timer_2.vo.Problem
import com.hc.problem_timer_2.dto.BookDto
import com.hc.problem_timer_2.dto.ProblemRecordDto
import com.hc.problem_timer_2.dto.toDto
import com.hc.problem_timer_2.dto.toVO
import com.hc.problem_timer_2.repository.BookRepository
import com.hc.problem_timer_2.repository.ProblemRecordRepository
import com.hc.problem_timer_2.vo.Grade
import com.hc.problem_timer_2.vo.ProblemRecord
import kotlinx.datetime.Instant
import timber.log.Timber
import javax.inject.Inject

class ProblemRecordRepositoryImpl @Inject constructor(private val problemRecordDao: ProblemRecordDao): ProblemRecordRepository {
    override suspend fun getAll(): List<ProblemRecord> = problemRecordDao.getAll().map { it.toVO() }
    override suspend fun getByBookId(bookId: Long) = problemRecordDao.getByBookId(bookId).map { it.toVO() }
    override suspend fun insert(problemRecord: ProblemRecord) = problemRecordDao.insert(problemRecord.toDto())
    override suspend fun update(problemRecord: ProblemRecord) = problemRecordDao.update(problemRecord.toDto())
    override suspend fun deleteById(id: Long) = problemRecordDao.deleteById(id)
}