package com.hc.problem_timer_2.repository

import com.hc.problem_timer_2.data_class.Problem
import com.hc.problem_timer_2.entity.Book

interface BookRepository {
    suspend fun getBooks(): List<Book>
    suspend fun insert(name: String, problems: List<Problem> = getDefaultProblems())
    suspend fun update(book: Book)
    suspend fun deleteById(id: Long)
}

fun getDefaultProblems() = (1 .. 100).map { page ->
    (1 .. 5).map { it.toString() }.map { number ->
        Problem(number, page)
    }
}.flatten()