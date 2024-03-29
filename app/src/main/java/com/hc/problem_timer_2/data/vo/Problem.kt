package com.hc.problem_timer_2.data.vo

data class Problem (
    val id: Long = 0L,
    val bookId: Long,
    val page: Int,
    val mainNumber: String,
    val subNumber: String? = null
): Comparable<Problem> {
    val number: String get() = if (isMainProblem()) mainNumber else "$mainNumber-$subNumber"
    fun isMainProblem() = subNumber == null

    override fun compareTo(other: Problem) = compareValuesBy(this, other,
        { it.mainNumber.toInt() },
        { if (it.isMainProblem()) 0 else it.subNumber!!.toInt() }
    )
}

fun List<Problem>.onBook(bookId: Long?) = filter { it.bookId == bookId }
fun List<Problem>.onPage(page: Int?) = filter { it.page == page }
