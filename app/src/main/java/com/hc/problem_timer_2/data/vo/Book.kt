package com.hc.problem_timer_2.data.vo

import com.hc.problem_timer_2.util.getNow
import kotlinx.datetime.LocalDateTime

data class Book(
    val id: Long = 0L,
    val name: String,
    val addedAt: LocalDateTime = getNow()
)