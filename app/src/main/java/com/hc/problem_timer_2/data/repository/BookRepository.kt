package com.hc.problem_timer_2.data.repository

import com.hc.problem_timer_2.data.vo.Book

interface BookRepository {
    suspend fun getAll(): List<Book>
    suspend fun insert(book: Book)
    suspend fun update(book: Book)
    suspend fun deleteById(id: Long)
}