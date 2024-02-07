package com.hc.problem_timer_2.repository_impl

import com.hc.problem_timer_2.dao.BookDao
import com.hc.problem_timer_2.vo.Book
import com.hc.problem_timer_2.vo.Problem
import com.hc.problem_timer_2.dto.BookDto
import com.hc.problem_timer_2.dto.toDto
import com.hc.problem_timer_2.dto.toVO
import com.hc.problem_timer_2.repository.BookRepository
import timber.log.Timber
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(private val bookDao: BookDao): BookRepository {
    override suspend fun getBooks() = bookDao.getAll().map { it.toVO() }
    override suspend fun insert(name: String, problems: List<Problem>) =
        bookDao.insert(BookDto(name = name, problems = problems))
    override suspend fun update(book: Book) {
        Timber.d("bookDto: ${book.toDto()}")
        return bookDao.update(book.toDto())
    }
    override suspend fun deleteById(id: Long) = bookDao.deleteById(id)
}