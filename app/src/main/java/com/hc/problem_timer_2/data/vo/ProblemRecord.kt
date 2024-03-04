package com.hc.problem_timer_2.data.vo

import kotlinx.datetime.LocalDateTime

data class ProblemRecord(
    val id: Long = 0L,
    val bookId: Long,
    val page: Int,
    val number: String,
    val timeRecord: Int,
    val grade: Grade,
    val solvedAt: LocalDateTime
) {
    fun isGraded() = grade != Grade.Unranked
}

fun List<ProblemRecord>.onBook(book: Book?) = filter { it.bookId == book?.id }
fun List<ProblemRecord>.onPage(page: Int?) = filter { it.page == page }