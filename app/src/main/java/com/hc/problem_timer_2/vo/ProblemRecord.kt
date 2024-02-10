package com.hc.problem_timer_2.vo

import kotlinx.datetime.LocalDateTime

data class ProblemRecord(
    val id: Long = 0L,
    val bookId: Long,
    val number: String,
    val timeRecord: Int,
    val grade: Grade,
    val solvedAt: LocalDateTime
)
