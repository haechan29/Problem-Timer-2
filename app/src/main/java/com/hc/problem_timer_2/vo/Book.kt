package com.hc.problem_timer_2.vo

data class Book(val id: Long = 0L, val name: String, var problems: List<Problem> = getDefaultProblems()) {
    fun getFirstPage() = problems[0].page
}

fun getDefaultProblems() = (1 .. 100).map { page ->
    ((page - 1) * 5 + 1 .. (page - 1) * 5 + 5).map { it.toString() }.map { number ->
        Problem(number, page)
    }
}.flatten()