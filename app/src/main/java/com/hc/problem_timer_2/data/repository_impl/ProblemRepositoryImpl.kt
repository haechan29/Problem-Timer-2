package com.hc.problem_timer_2.data.repository_impl

import com.hc.problem_timer_2.data.dao.ProblemDao
import com.hc.problem_timer_2.data.vo.Problem
import com.hc.problem_timer_2.data.dto.toDto
import com.hc.problem_timer_2.data.dto.toVO
import com.hc.problem_timer_2.data.repository.ProblemRepository
import javax.inject.Inject

class ProblemRepositoryImpl @Inject constructor(private val problemDao: ProblemDao): ProblemRepository {
    override suspend fun getAll() = problemDao.getAll().map { it.toVO() }
    override suspend fun getByBookId(bookId: Long) = problemDao.getByBookId(bookId).map { it.toVO() }
    override suspend fun insert(problem: Problem) = problemDao.insert(problem.toDto())
    override suspend fun insertAll(problems: List<Problem>) = problemDao.insertAll(*problems.map { it.toDto() }.toTypedArray())
    override suspend fun update(problem: Problem) = problemDao.update(problem.toDto())
    override suspend fun delete(problem: Problem) = problemDao.delete(problem.toDto())
    override suspend fun deleteAll(problems: List<Problem>) = problemDao.deleteAll(*problems.map { it.toDto() }.toTypedArray())
}