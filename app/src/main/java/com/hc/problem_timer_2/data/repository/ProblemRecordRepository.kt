package com.hc.problem_timer_2.data.repository

import com.hc.problem_timer_2.data.vo.ProblemRecord

interface ProblemRecordRepository {
    suspend fun getAll(): List<ProblemRecord>
    suspend fun getByBookId(bookId: Long): List<ProblemRecord>
    suspend fun upsert(problemRecord: ProblemRecord)
    suspend fun deleteById(id: Long)
}