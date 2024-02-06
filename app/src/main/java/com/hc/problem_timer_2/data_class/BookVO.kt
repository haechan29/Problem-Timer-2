package com.hc.problem_timer_2.data_class

data class BookVO(val id: Long, val name: String, var problems: List<Problem>) {
    fun getFirstPage() = problems[0].page
}