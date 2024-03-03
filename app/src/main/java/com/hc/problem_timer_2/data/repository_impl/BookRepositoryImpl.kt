package com.hc.problem_timer_2.data.repository_impl

import com.hc.problem_timer_2.data.dao.BookDao
import com.hc.problem_timer_2.data.vo.Book
import com.hc.problem_timer_2.data.dto.toDto
import com.hc.problem_timer_2.data.dto.toVO
import com.hc.problem_timer_2.data.repository.BookRepository
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(private val bookDao: BookDao): BookRepository {
    override suspend fun getAll() = bookDao.getAll().map { it.toVO() }
    override suspend fun insert(book: Book) = bookDao.insert(book.toDto())
    override suspend fun update(book: Book) = bookDao.update(book.toDto())
    override suspend fun deleteById(id: Long) = bookDao.deleteById(id)
}