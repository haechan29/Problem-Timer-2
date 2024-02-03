package com.hc.problem_timer_2.data_class

data class Book(
    val name: String,
    var problems: List<Problem> = (1 .. 10).map { page ->
        (1 .. 5).map { number ->
            Problem(((page - 1) * 5 + number).toString(), page)
        }
    }.flatten()
) {
    fun getFirstPage() = problems[0].page
}