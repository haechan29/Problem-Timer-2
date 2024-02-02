package com.hc.problem_timer_2.data_class

data class Book(
    val name: String,
    val pages: List<Int> = (1..100).toList()
)