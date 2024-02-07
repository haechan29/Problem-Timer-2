package com.hc.problem_timer_2.repository_impl

import com.hc.problem_timer_2.dao.BookDao
import com.hc.problem_timer_2.data_class.BookVO
import com.hc.problem_timer_2.data_class.Problem
import com.hc.problem_timer_2.entity.Book
import com.hc.problem_timer_2.entity.toDto
import com.hc.problem_timer_2.entity.toVO
import com.hc.problem_timer_2.repository.BookRepository
import com.hc.problem_timer_2.util.added
import com.hc.problem_timer_2.util.updated
import timber.log.Timber
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(private val bookDao: BookDao): BookRepository {
    override suspend fun getBooks() = bookDao.getBooks().map { it.toVO() }
    override suspend fun insert(name: String, problems: List<Problem>) =
        bookDao.insert(Book(name = name, problems = problems))
    override suspend fun update(bookVO: BookVO) {
        Timber.d("bookDto: ${bookVO.toDto()}")
        return bookDao.update(bookVO.toDto())
    }
    override suspend fun deleteById(id: Long) = bookDao.deleteById(id)
}