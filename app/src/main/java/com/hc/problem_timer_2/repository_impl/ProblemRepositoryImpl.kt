package com.hc.problem_timer_2.repository_impl

import com.hc.problem_timer_2.dao.ProblemDao
import com.hc.problem_timer_2.vo.Problem
import com.hc.problem_timer_2.dto.toDto
import com.hc.problem_timer_2.dto.toVO
import com.hc.problem_timer_2.repository.ProblemRepository
import javax.inject.Inject

class ProblemRepositoryImpl @Inject constructor(private val problemDao: ProblemDao): ProblemRepository {
    override suspend fun getAll() = problemDao.getAll().map { it.toVO() }
    override suspend fun getByBookId(bookId: Long) = problemDao.getByBookId(bookId).map { it.toVO() }
    override suspend fun insert(problem: Problem) = problemDao.insert(problem.toDto())
    override suspend fun insertAll(problems: List<Problem>) = problemDao.insertAll(*problems.map { it.toDto() }.toTypedArray())
    override suspend fun update(problem: Problem) = problemDao.update(problem.toDto())
    override suspend fun delete(problem: Problem) = problemDao.delete(problem.toDto())
}