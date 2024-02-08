package com.hc.problem_timer_2.repository

import com.hc.problem_timer_2.vo.Book
import com.hc.problem_timer_2.vo.Problem

interface BookRepository {
    suspend fun getAll(): List<Book>
    suspend fun insert(book: Book)
    suspend fun update(book: Book)
    suspend fun deleteById(id: Long)
}