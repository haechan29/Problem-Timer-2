package com.hc.problem_timer_2.repository

import com.hc.problem_timer_2.vo.Book
import com.hc.problem_timer_2.vo.Problem

interface ProblemRepository {
    suspend fun getByBookIdAndPage(bookId: Long, page: Int): List<Problem>
    suspend fun insert(problem: Problem)
    suspend fun update(problem: Problem)
    suspend fun delete(problem: Problem)
}