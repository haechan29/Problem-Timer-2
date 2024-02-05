package com.hc.problem_timer_2.data_class

data class BookVO(val name: String, var problems: List<Problem> = getDefaultProblems()) {
    fun getFirstPage() = problems[0].page
}

fun getDefaultProblems() = (1 .. 100).map { page ->
    (1 .. 5).map { it.toString() }.map { number ->
        Problem(number, page)
    }
}.flatten()