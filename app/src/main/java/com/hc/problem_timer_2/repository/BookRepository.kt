package com.hc.problem_timer_2.repository

import com.hc.problem_timer_2.vo.Book
import com.hc.problem_timer_2.vo.Problem

interface BookRepository {
    suspend fun getBooks(): List<Book>
    suspend fun insert(name: String, problems: List<Problem> = getDefaultProblems())
    suspend fun update(book: Book)
    suspend fun deleteById(id: Long)
}

fun getDefaultProblems() = (1 .. 100).map { page ->
    ((page - 1) * 5 + 1 .. (page - 1) * 5 + 5).map { it.toString() }.map { number ->
        Problem(number, page)
    }
}.flatten()