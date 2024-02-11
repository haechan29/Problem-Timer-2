package com.hc.problem_timer_2.repository_impl

import com.hc.problem_timer_2.dao.BookDao
import com.hc.problem_timer_2.dao.ProblemDao
import com.hc.problem_timer_2.vo.Book
import com.hc.problem_timer_2.vo.Problem
import com.hc.problem_timer_2.dto.BookDto
import com.hc.problem_timer_2.dto.toDto
import com.hc.problem_timer_2.dto.toVO
import com.hc.problem_timer_2.repository.BookRepository
import com.hc.problem_timer_2.repository.ProblemRepository
import timber.log.Timber
import javax.inject.Inject

class ProblemRepositoryImpl @Inject constructor(private val problemDao: ProblemDao): ProblemRepository {
    override suspend fun getByBookIdAndPage(bookId: Long, page: Int) = problemDao.getByBookIdAndPage(bookId, page).map { it.toVO() }
    override suspend fun insert(problem: Problem) = problemDao.insert(problem.toDto())
    override suspend fun update(problem: Problem) = problemDao.update(problem.toDto())
    override suspend fun delete(problem: Problem) = problemDao.delete(problem.toDto())
}