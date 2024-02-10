package com.hc.problem_timer_2.repository

import com.hc.problem_timer_2.vo.Book
import com.hc.problem_timer_2.vo.Grade
import com.hc.problem_timer_2.vo.Problem
import com.hc.problem_timer_2.vo.ProblemRecord
import kotlinx.datetime.Instant

interface ProblemRecordRepository {
    suspend fun getAll(): List<ProblemRecord>
    suspend fun getByBookId(bookId: Long): List<ProblemRecord>
    suspend fun insert(problemRecord: ProblemRecord)
    suspend fun update(problemRecord: ProblemRecord)
    suspend fun deleteById(id: Long)
}