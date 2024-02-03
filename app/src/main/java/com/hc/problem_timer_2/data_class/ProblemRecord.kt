package com.hc.problem_timer_2.data_class

import java.time.LocalDateTime

data class ProblemRecord(
    val number: String,
    val timeRecord: Int,
    val grade: Grade,
    val solvedAt: LocalDateTime
)
