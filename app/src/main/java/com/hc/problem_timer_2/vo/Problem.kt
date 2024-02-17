package com.hc.problem_timer_2.vo

data class Problem(val id: Long = 0L, val bookId: Long, val page: Int, val mainNumber: String, val subNumber: String? = null) {
    fun isMainProblem() = subNumber == null
    fun getNumber() = if (isMainProblem()) mainNumber else "$mainNumber-$subNumber"
}
