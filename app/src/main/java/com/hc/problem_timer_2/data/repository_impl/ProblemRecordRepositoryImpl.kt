package com.hc.problem_timer_2.data.repository_impl

import com.hc.problem_timer_2.data.dao.ProblemRecordDao
import com.hc.problem_timer_2.data.dto.toDto
import com.hc.problem_timer_2.data.dto.toVO
import com.hc.problem_timer_2.data.repository.ProblemRecordRepository
import com.hc.problem_timer_2.data.vo.ProblemRecord
import javax.inject.Inject

class ProblemRecordRepositoryImpl @Inject constructor(private val problemRecordDao: ProblemRecordDao): ProblemRecordRepository {
    override suspend fun getAll(): List<ProblemRecord> = problemRecordDao.getAll().map { it.toVO() }
    override suspend fun getByBookId(bookId: Long) = problemRecordDao.getByBookId(bookId).map { it.toVO() }
    override suspend fun insert(problemRecord: ProblemRecord) = problemRecordDao.insert(problemRecord.toDto())
    override suspend fun update(problemRecord: ProblemRecord) = problemRecordDao.update(problemRecord.toDto())
    override suspend fun deleteById(id: Long) = problemRecordDao.deleteById(id)
}