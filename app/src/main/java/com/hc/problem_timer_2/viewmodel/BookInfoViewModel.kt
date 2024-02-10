package com.hc.problem_timer_2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hc.problem_timer_2.vo.Book
import com.hc.problem_timer_2.vo.Problem
import com.hc.problem_timer_2.repository.BookRepository
import com.hc.problem_timer_2.repository.ProblemRecordRepository
import com.hc.problem_timer_2.util.updated
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.IndexOutOfBoundsException

@HiltViewModel
class BookInfoViewModel @Inject constructor(private val problemRecordRepository: ProblemRecordRepository): ViewModel() {
    private val _bookInfo = MutableLiveData<BookInfo>()
    val bookInfo: LiveData<BookInfo> get() = _bookInfo

    fun setBook(book: Book) { _bookInfo.value = BookInfo(book = book, currentPage = book.getFirstPage()) }
    fun setCurrentPage(page: Int) { _bookInfo.value = withBookInfoNotNull { it.copy(currentPage = page) } }
    fun isProblemNumberDuplicated(number: String) = withBookInfoNotNull { number in it.getProblemsOnCurrentPage().map { it.number } }

    fun updateProblemNumber(problem: Problem, newNumber: String) {
        _bookInfo.value = withBookInfoNotNull { bookInfo ->
            val problems = bookInfo.getBook().problems
            if (problem !in problems) throw IndexOutOfBoundsException("problem out of problems")
            val index = problems.indexOf(problem)
            val newProblems = problems.updated(index, problem.copy(number = newNumber))
            val newBook = bookInfo.getBook().copy(problems = newProblems)
            bookInfo.copy(book = newBook)
        }
    }

    private fun <R> withBookInfoNotNull(f: (BookInfo) -> R): R {
        if (bookInfo.value == null) throw UninitializedPropertyAccessException("bookInfo not initialized")
        return f(bookInfo.value!!)
    }
}

data class BookInfo(private val book: Book, private val currentPage: Int) {
    fun getBook() = book
    fun getCurrentPage() = currentPage
    fun getProblemsOnCurrentPage() = book.problems.filter { problem -> problem.page == currentPage }
    fun getPages() = book.problems.map { problem -> problem.page }.distinct()
}
