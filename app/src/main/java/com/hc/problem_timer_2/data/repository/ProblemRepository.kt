package com.hc.problem_timer_2.data.repository

import com.hc.problem_timer_2.data.vo.Problem

interface ProblemRepository {
    suspend fun getAll(): List<Problem>
    suspend fun getByBookId(bookId: Long): List<Problem>
    suspend fun insert(problem: Problem)
    suspend fun insertAll(problems: List<Problem>)
    suspend fun update(problem: Problem)
    suspend fun delete(problem: Problem)
    suspend fun deleteAll(problems: List<Problem>)
}