package com.hc.problem_timer_2.vo

data class Book(val id: Long, val name: String, var problems: List<Problem>) {
    fun getFirstPage() = problems[0].page
}