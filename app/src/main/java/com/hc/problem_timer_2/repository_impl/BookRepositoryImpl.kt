package com.hc.problem_timer_2.repository_impl

import com.hc.problem_timer_2.dao.BookDao
import com.hc.problem_timer_2.data_class.Problem
import com.hc.problem_timer_2.entity.Book
import com.hc.problem_timer_2.repository.BookRepository
import com.hc.problem_timer_2.util.added
import com.hc.problem_timer_2.util.updated
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(private val bookDao: BookDao): BookRepository {
    override suspend fun getBooks() = bookDao.getBooks()
    override suspend fun insert(name: String, problems: List<Problem>) =
        bookDao.insert(Book(name = name, problems = problems))
    override suspend fun update(book: Book) = bookDao.update(book)
    override suspend fun delete(book: Book) = bookDao.delete(book)
}